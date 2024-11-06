const sequelize = require("../config/database");
const { Post, Hashtag, Comment } = require("../models");
const Sequelize = require("sequelize");

function extractHashtags(content) {
  const regex = /#(\w{2,})/g;
  const matches = [];
  let match;

  while ((match = regex.exec(content)) !== null) {
    matches.push(match[1]);
  }

  return matches;
}

function formatPost(post) {
  const { distance, location, ...rest } = post.toJSON();
  return {
    ...rest,
    location: {
      longitude: parseFloat(location.coordinates[0]),
      latitude: parseFloat(location.coordinates[1]),
      distance: parseFloat(distance),
    },
  };
}

async function getPost(id, latitude, longitude) {
  if (!id || !latitude || !longitude) {
    return null;
  }

  try {
    const post = await Post.findOne({
      where: { id },
      attributes: {
        include: [
          [
            Sequelize.fn(
              "ST_DistanceSphere",
              Sequelize.col("location"),
              Sequelize.literal(`ST_GeomFromText(:coordinates)`)
            ),
            "distance",
          ],
          [Sequelize.fn('COUNT', Sequelize.col("Comments.id")), 'commentCount']
        ],
      },
      include: [{
        model: Comment,
        attributes: []
      }],
      replacements: {
        coordinates: `POINT(${longitude} ${latitude})`,
      },
      group: ['Post.id']
    });

    return formatPost(post);
  } catch (error) {
    throw new Error(`There was a error getting the post. ${error.message}`);
  }
}

async function getPostsByHashtag(
  hashtag,
  latitude,
  longitude,
  sortOrder = "ASC"
) {
  if (!hashtag || !latitude || !longitude) {
    throw new Error(
      "Hashtag to search for and the user's current location (latitude and longitude) must be provided."
    );
  }
  try {
    const currentDate = new Date();
    const posts = await Post.findAll({
      include: [
        {
          model: Hashtag,
          where: {
            name: {
              [Sequelize.Op.iLike]: hashtag,
            },
          },
          attributes: [],
        },
        {
          model: Comment,
          attributes: []
        }
      ],
      where: {
        [Sequelize.Op.or]: [
          {
            expiryDate: {
              [Sequelize.Op.gt]: currentDate,
            },
          },
          { expiryDate: null },
        ],
      },
      attributes: {
        include: [
          [
            Sequelize.fn(
              "ST_DistanceSphere",
              Sequelize.col("location"),
              Sequelize.literal(`ST_GeomFromText(:coordinates)`)
            ),
            "distance",
          ],
          [Sequelize.fn('COUNT', Sequelize.col("Comments.id")), 'commentCount']
        ],
      },
      replacements: {
        coordinates: `POINT(${longitude} ${latitude})`,
      },
      order: [
        ["created_at", "DESC"],
        [Sequelize.literal("distance"), "ASC"],
      ],
      group: ['Post.id']
    });

    const formattedPosts = posts.map(formatPost);

    return formattedPosts;
  } catch (error) {
    console.error("Error fetching posts with hashtag", error);
    throw new Error("There was an error trying to fetch posts with hashtag.");
  }
}

async function getAllPosts(latitude, longitude, sortOrder = "ASC") {
  try {
    const currentDate = new Date();
    const posts = await Post.findAll({
      where: {
        [Sequelize.Op.or]: [
          {
            expiryDate: {
              [Sequelize.Op.gt]: currentDate,
            },
          },
          { expiryDate: null },
        ],
      },
      include: [{
        model: Comment,
        attributes: []
      }],
      attributes: {
        include: [
          [
            Sequelize.fn(
              "ST_DistanceSphere",
              Sequelize.col("location"),
              Sequelize.literal(`ST_GeomFromText(:coordinates)`)
            ),
            "distance",
            [Sequelize.fn('COUNT', Sequelize.col("Comments.id")), 'commentCount']
          ],
        ],
      },
      replacements: {
        coordinates: `POINT(${longitude} ${latitude})`,
      },
      order: [
        ["created_at", "DESC"],
        [Sequelize.literal("distance"), sortOrder],
      ],
      group: ['Post.id']
    });
    const formattedPosts = posts.map(formatPost);
    return formattedPosts;
  } catch (error) {
    throw new Error(
      `There was an error getting all posts. Error: ${error.message}`
    );
  }
}

async function getPostsWithinDistance(
  distance,
  latitude,
  longitude,
  sortOrder = "ASC"
) {
  if (!distance || !latitude || !longitude) {
    throw new Error("Distance, latitude, and longitude must all be provided.");
  }

  try {
    const currentDate = new Date();
    const posts = await Post.findAll({
      where: {
        [Sequelize.Op.and]: [
          // Filter for posts within the specified distance and valid expiry dates
          Sequelize.where(
            Sequelize.fn(
              "ST_DistanceSphere",
              Sequelize.col("location"),
              Sequelize.literal(`ST_GeomFromText(:coordinates)`)
            ),
            { [Sequelize.Op.lte]: distance }
          ),
          {
            [Sequelize.Op.or]: [
              { expiryDate: { [Sequelize.Op.gt]: currentDate } },
              { expiryDate: null },
            ],
          },
        ],
      },
      replacements: {
        coordinates: `POINT(${longitude} ${latitude})`,
      },
      include: [
        {
          model: Comment,
          attributes: [], // We don’t need individual comment details, just the count
        },
      ],
      attributes: {
        include: [
          [
            Sequelize.fn(
              "ST_DistanceSphere",
              Sequelize.col("location"),
              Sequelize.literal(`ST_GeomFromText(:coordinates)`)
            ),
            "distance",
          ],
          [Sequelize.fn('COUNT', Sequelize.col("Comments.id")), 'commentCount'],
        ],
      },
      group: ['Post.id'],
      order: [
        ["created_at", "DESC"],
        [Sequelize.literal("distance"), sortOrder],
      ],
    });

    const formattedPost = posts.map(formatPost);

    return formattedPost;
  } catch (error) {
    throw new Error(`There was an error fetching posts: ${error.message}`);
  }
}

async function createPost(
  text,
  authorId,
  expiryDate,
  postLongitude,
  postLatitude,
  userLongitude,
  userLatitude
) {
  const location = {
    type: "Point",
    coordinates: [postLongitude, postLatitude],
  };
  const hashtags = extractHashtags(text);

  try {
    const result = await sequelize.transaction(async (t) => {
      const post = await Post.create(
        {
          text: text,
          authorId,
          expiryDate,
          location,
        },
        { transaction: t }
      );

      await Hashtag.bulkCreate(
        hashtags.map((hashtag) => ({ name: hashtag, postId: post.id })),
        { transaction: t }
      );

      return post;
    });
    const fetchedPost = await getPost(result.id, userLatitude, userLongitude);
    return fetchedPost;
  } catch (error) {
    throw new Error(`Error creating post with hashtags: ${error.message}`);
  }
}

async function deletePost(id) {
  try {
    await Post.destroy({
      where: {
        id,
      },
    });

    return true;
  } catch (error) {
    throw new Error(`Error deleting post(${id}): ${error.message}`);
  }
}

module.exports = {
  createPost,
  getPost,
  getAllPosts,
  getPostsWithinDistance,
  getPostsByHashtag,
  deletePost,
};

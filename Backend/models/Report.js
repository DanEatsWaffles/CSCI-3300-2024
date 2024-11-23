const { DataTypes } = require("sequelize");
const sequelize = require("../config/database");

const Report = sequelize.define(
  "Report",
  {
    snitch: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    postId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: "post_id"
    },
  },
  {
    modelName: "Report",
    tableName: "reports",
    indexes: [
      {
        unique: true,
        fields: ["snitch", "post_id"],
        name: "unique_snitch_per_post"
      },
    ],
  }
);

module.exports = Report;

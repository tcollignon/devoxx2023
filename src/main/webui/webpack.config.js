const CopyPlugin = require('copy-webpack-plugin')
const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin');
const fs = require("fs");

const commonConfig = {
  mode: 'development',
  entry: {
    index: './src/index.js'
  },
  output: {
    filename: '[name].[contenthash].js',
    publicPath: '/',
    path: path.resolve(__dirname, 'dist'),
    clean: true
  },
  optimization: {
    moduleIds: 'deterministic',
    runtimeChunk: 'single',
    splitChunks: {
      cacheGroups: {
          vendor: {
              test: /[\\/]node_modules[\\/]/,
              name: 'vendors',
              chunks: 'all',
          },
      },
    },
  },
  module: {
    rules: [
      {
        test: /jeton\.svg$/,
        type: 'asset/source'
      }
    ]
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: 'public', to: '' }
      ]
    }),
      new HtmlWebpackPlugin({
          title: 'Output Management',
          template: 'src/index.ejs'
      })
  ],
  resolve: {
    modules: [path.resolve(__dirname, 'node_modules')]
  }
}

module.exports = () => {
    return commonConfig
}

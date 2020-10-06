const htmlWebpackPlugin = require('html-webpack-plugin');

const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');

const webpack = require('webpack');

const path = require('path');

module.exports = {
    entry: "./src/index.tsx",
    devtool: 'cheap-module-source-map',
    output: {
        path: path.resolve(__dirname, "build"),
        filename: "bundle.js",
    },
    resolve: {
        extensions: [".js", ".jsx", ".json", ".ts", ".tsx"],
    },
    module: {
        rules: [
            {
                test: /\.(j|t)sx?$/,
                exclude: /node_modules/,
                loader: "babel-loader",
                options: {
                    cacheDirectory: true,
                    babelrc: false,
                    presets: [
                        [
                            "@babel/preset-env",
                            {targets: {browsers: "last 2 versions"}}
                        ],
                        "@babel/preset-typescript",
                        "@babel/preset-react"
                    ],
                    plugins: [
                        ["@babel/plugin-proposal-decorators", {legacy: true}],
                        ["@babel/plugin-proposal-class-properties", {loose: true}],
                        "react-hot-loader/babel"
                    ]
                }
            },
            {
                test: /\.css$/, use: [
                    {loader: 'style-loader'},
                    {loader: 'css-loader'}
                ]
            },
            {
                test: /\.(jpg|png|gif)$/, use: [{loader: 'file-loader'}]
            }
        ]
    },
    devServer: {
        historyApiFallback: {
            index: '/',
        },
        hot: true
    },
    plugins: [
        new htmlWebpackPlugin({
            template: 'index.html'
        }),
        new ForkTsCheckerWebpackPlugin()
    ]
};
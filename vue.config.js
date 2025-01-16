const path = require('path');

module.exports = {
  runtimeCompiler: true,
  lintOnSave: false,
  transpileDependencies: [],
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src/')
      }
    }
  }
};

(function() {
  require.config({
    baseUrl: "/js",
    paths: {
      js: '/js/',
      jQuery: '/js/lib/jquery-1.6.3',
      Underscore: '/js/lib/underscore',
      Backbone: '/js/lib/backbone_loader'
    }
  });
  define(['js/bkeeping/models'], function(models) {
    console.log('bkeeping LOADED');
    return {
      models: models
    };
  });
}).call(this);

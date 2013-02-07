
define( [
          #'order!js/lib/jquery-1.6.3',
          #'order!js/lib/pure',
          #'order!js/lib/json2',
          #'order!js/lib/underscore',
          #'order!js/lib/backbone',
          'bkeeping/models',
          'bkeeping/views',
          #'order!js/lib/jquery.dataTables',
          ]
  #(a, b, c, d, e, models, views, f) ->
  (models, views) ->
    
    console.log('bkeeping LOADED')
    
    # return an object with the models in it 
    models : models
    views : views
    #jQuery : jQuery.noConflict()
    #Underscore : _.noConflict()
    #Backbone : Backbone.noConflict()
)


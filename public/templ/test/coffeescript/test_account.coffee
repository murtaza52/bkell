
define(['js/bkeeping/models'], (models) ->
  
  ping : ->
    console.log("bkeeping/models loaded: #{models}")
  
  testC : ->
    
    console.log("test_account.testC CALLED")
    
    acct = new models.Account()
    acct.set(
      "tag":"account"
      "type":"asset"
      "id":"cash"
      "name":"cash"
      "counterWeight":"debit"
    )
    acct.saveS()
    
    # TODO - callback to handle {"message":"User is not authenticated","tag":"error"}
  
  testR : ->
  
    console.log("test_account.testR CALLED")
    
    acct = new models.Account()
    acct.set( "id":"cash")
    acct.fetchS()
    
  testU : ->
  
    console.log("test_account.testU CALLED")
    
    acct = new models.Account()
    acct.set( "id":"cash")
    acct.fetchS(  success: (model, response) ->
                    acct.set( "counterWeight":"credit" )
                    acct.saveS( {}, type : 'POST' )
    )
  
  testD : ->
  
    console.log("test_account.testD CALLED")
    
  testList : ->
    
    ###
    console.log("test_account.testList CALLED")
    
    accounts = new models.Accounts()
    accounts.fetchS()
    ###
    
)


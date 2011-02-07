
(defproject bkell-clj "pre-alpha"
   :description "A Clojure implementation of the bookkeeping project"
   
   :dependencies  [  	[org.clojure/clojure "1.2.0"]
						[org.clojure/clojure-contrib "1.2.0"]
                        [compojure "0.5.2"]
                        [ring/ring-jetty-adapter "0.3.3"]
                        [log4j/log4j "1.2.7"]
                        [sablecc "3.2"]
                        [clj-stacktrace "0.1.2"]
                        [clojure-http-client "1.1.0-SNAPSHOT"]
                        [com.interrupt/bookkeeping "0.0.0-prealpha"]
                        [com.interrupt/bob "0.0.0-prealpha"]
				  ]

  :dev-dependencies  [ [org.exist/exist "1.4"]
                       [org.exist/exist-modules "1.4"]
                       [org.exist/exist-ngram-module "1.4"]
                       [antlr/antlr "2.7.7"] 
                       [org.apache/commons-pool "1.5.1"]
                       [org.apache/commons-collections "3.2.1"]
                       [org.apache/commons-logging "1.1.1"]
                       [jgroups/jgroups-all "2.2.7"]
                       [log4j/log4j "1.2.15"]
                       [quartz/quartz "1.6.5"]
                       [sunxacml/sunxacml "1.2"]
                       [xmldb/xmldb "unknown"]
                       [xmlrpc/xmlrpc-client "3.0"]
                       [xmlrpc/xmlrpc-common "3.0"]
                       [xmlrpc/xmlrpc-server "3.1.2"]
                       [javax.transaction/jta "1.1"]
                       [xerces/xercesImpl "2.6.2"]
                       [xml-resolver/xml-resolver "1.2"]
                    ] 
	
)


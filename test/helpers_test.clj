(ns helper-test
	(:use [helpers] :reload-all)
	(:use [clojure.test])
	(:import java.io.ByteArrayInputStream) 
	(:require clojure.contrib.str-utils)
)


(def configs (load-file "etc/config/config.test.clj"))


;; test HTTP GET 
(deftest test-http-get 
	
	(let [result-GET (execute-http-call 
		(str (:url-test configs) "/thing") 
		"GET"
		{ "Content-Type" "text/xml" } 
		nil)]
		
		(println "test result [" result-GET "]")
		(is (not (nil? result-GET)) "GET result should not be nil")
		(is (. (:msg result-GET ) equals "OK"))
		(is (. (:code result-GET ) equals 200))
		
		(let [ parsed-test (clojure.xml/parse (ByteArrayInputStream. (.getBytes 
			(clojure.contrib.str-utils/str-join nil (:body-seq result-GET ))		;; get the XML string  
			"UTF-8")))] 
			
			(is 	;; test that there is a 'thing' keyword , that can evaluate on a hash 
				(. ((:tag parsed-test) {:thing "result" }) equals 
				"result")
			)
			
		)
	)
)


;; test HTTP PUT 
(deftest test-http-put 
	
	;; test good request 
	(let [result-PUT (execute-http-call 
		(str (:url-test configs) "/test/test-good-http-put") 
		"PUT" 
		{	"Content-Type" "text/xml" "Authorization" (str "Basic " (:passwdBase64 configs)) } 
		"<test-good-http-put/>" 
		)]
		
		(println "test result [" result-PUT "]")
		(is (not (nil? result-PUT)) "PUT result should not be nil") 
		(is (. (:code result-PUT ) equals 201) "response code SHOULD be 201" )
		(is (. (:msg result-PUT ) equals "Created") "test xml should have been 'Created'" )
	)
	
	;; test bad passwd 
	(let [result-PUT (execute-http-call 
		(str (:url-test configs) "/test/test-bad-passwd") 
		"PUT" 
		{	"Content-Type" "text/xml" "Authorization" "Basic badpasswd" } 
		"<test-bad-passwd/>" 
		)]
		
		(println "test result [" result-PUT "]")
		(is (not (nil? result-PUT)) "PUT result should not be nil") 
		(is (. (:code result-PUT ) equals 500) "response code SHOULD be 500" )
		(is (. (:msg result-PUT ) equals "Error") "test PUT should return 'Error'" )
	)
	
	;; test no Authorization	TODO - this should fail 
	(comment (let [result-PUT (execute-http-call 
		(str (:url-test configs) "/test/test-no-authorization" ) 
		"PUT" 
		{	"Content-Type" "text/xml" } 
		"<test-no-authorization/>" 
		)]
		
		(println "test result [" result-PUT "]")
		(is (not (nil? result-PUT)) "PUT result should not be nil") 
		(is (. (:code result-PUT ) equals 401) "response code SHOULD be 201" )
		(is (. (:msg result-PUT ) equals "Created") "test xml should have been 'Created'" )
	))
	
)

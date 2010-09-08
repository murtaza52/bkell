(defn add-user [db-base-URL db-system-DIR working-USER] 
		
		;; 1. check that there's not an existing user 
		(let [check-user
						(execute-http-call 	;; TODO - put in 404 check 
							(str 
								db-base-URL 
								db-system-DIR 
								(working-dir-lookup (:tag working-USER)) 	;; stringing together lookup URL leaf 
								"/" 
								(str 
									(name (:tag working-USER)) 
									"." 
									(:id (:attrs working-USER)))
								"/"
								(str 			;; repeating user name as leaf document 
									(name (:tag working-USER)) 
									"." 
									(:id (:attrs working-USER))))
								"GET" 
								{"Content-Type" "text/xml"} 
								nil
						)
				 ]
			
			(println "check-user[" check-user "]")	;; TODO - if <error/>, ADD user; user exists otherwise 
			
			(if (not (= (:msg check-user) "Error"))
					
					(println "user ALREADY exists") ;; TODO - throw error to user
					
					(do 
						
						(println "ADDING user[" working-USER "]")  
						
						;; 2. add to aauth.groups and corresponding default group to the new user
						(let [aauth-group (clojure.xml/parse "etc/xml/add.group.xml")] 
							
							(println "...loading add.group.xml[" aauth-group "]")
							(let [local-id 	(str 
												(name (:tag working-USER)) 
												"." 
												(:id (:attrs working-USER)))] 
										
										(let [db-group 	(assoc aauth-group 
																						:attrs 	{	
																											:id local-id , 
																											:name local-id , 
																											:owner (:id (:attrs working-USER)) 
																										}, 
																				 		:content 	[ 
																				 								{ 
																				 									:tag (keyword "user"), 
																				 									:attrs 	{ 
																		 																:xmlns "com/interrupt/bookkeeping/users", 
																		 																:id (:id (:attrs working-USER)) 
																		 															}
																				 								} 
																				 						 	] 
																		)
															]
															
												
												;; PUT to eXist 
												(println "CREATing group [" db-group "] / XML[" (with-out-str (clojure.xml/emit db-group)) "]" )
												(execute-http-call 		
																							(str db-base-URL db-system-DIR (working-dir-lookup :group)
																															"/" "group." (:id (:attrs working-USER)) 
																															"/" "group." (:id (:attrs working-USER)))
																							"PUT" 
																							{	"Content-Type" "text/xml"
																								"Authorization" "Basic YWRtaW46"}
																							(with-out-str (clojure.xml/emit db-group))
																						
													
												)
												
												;; 3. add to aauth.users ... PUT to eXist 
												;; 4. profile Details ... PUT to eXist	... TODO 
												(println "CREATing user [" working-USER "] / XML[" (with-out-str (clojure.xml/emit working-USER)) "]" )
												(execute-http-call 		
																							(str db-base-URL db-system-DIR (working-dir-lookup :user)
																															"/" "user." (:id (:attrs working-USER)) 
																															"/" "user." (:id (:attrs working-USER)))
																							"PUT" 
																							{	"Content-Type" "text/xml"
																								"Authorization" "Basic YWRtaW46"}
																							(with-out-str (clojure.xml/emit working-USER))
												)
												
												;; 5. add associated Bookkeeping to Group ... PUT to eXist 
												(execute-http-call 		
																							(str db-base-URL db-system-DIR (working-dir-lookup :bookkeeping)
																															"/" "group." (:id (:attrs working-USER)) 
																															"/bookkeeping.main.bookkeeping/bookkeeping.main.bookkeeping" )
																							"PUT" 
																							{	"Content-Type" "text/xml"
																								"Authorization" "Basic YWRtaW46"}
																							(slurp "etc/xml/default.bookkeeping.xml" )
												)
										)
								)
						)
						
			)
			)
	)
)

(defn add-generic [db-base-URL db-system-DIR working-ITEM]
		
		
		;; ... TODO - logic to build XQuery to use to insert 
		
		;; PUT to eXist 
		(println "CREATing [" working-ITEM "] / XML[" (with-out-str (clojure.xml/emit working-ITEM)) "]" )
		(execute-http-call 		
													(str db-base-URL db-system-DIR (working-dir-lookup :bookkeeping)
																					"/" "group." (:id (:attrs working-ITEM)) 
																					"/bookkeeping.main.bookkeeping/bookkeeping.main.bookkeeping" 
																					"?_wrap=no&_query="
																					)
													"POST" 
													{	"Content-Type" "text/xml"
														"Authorization" "Basic YWRtaW46" }
													(slurp "etc/xml/default.bookkeeping.xml" )
		)
)


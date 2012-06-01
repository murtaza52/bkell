(ns bkell.commands.get

  (:use #_[somnium.congomongo]
        [monger operators conversion]
  )
  (:import  [com.mongodb WriteResult WriteConcern DBCursor DBObject CommandResult$CommandFailure MapReduceOutput MapReduceCommand MapReduceCommand$OutputType]
  )
  (:require [bkell.domain]
            [monger.collection :as mc]
  )
)


;; get user 
(defn get-user [uname]
  
  (let [result {} #_(first (fetch "users" :where { :username uname }))]
    
    (if (-> result empty? not)
      (bkell.domain/keywordize-tags result)
      nil
    )
  )
)
(defn get-group [uname]
  (first () #_(fetch "groups" :where { :owner uname }))
)
(defn get-bookkeeping [uname] 
  (first () #_(fetch "bookkeeping" :where { :owner uname }))
)


;; get currency 
(defn get-currencies [uname] 

  #_(let [m (str "function(){ 
			  if( this.owner == '"uname"' ) { 
			    this.content[0].content.forEach( 
			      function(x) { 
			        emit( this.owner , x ); 
			      }
			    );
			  }
			};")
        r   "function(k,vals) { return { result : vals } ; }"
        result {} #_(map-reduce :bookkeeping m r {:inline 1})]
    
    (vec (map bkell.domain/keywordize-tags 
      (-> result first :value :result)))  ;; dig in and get the currency list 
  )
  #_(fetch "currencies")
)
(defn get-currency [uname currency]

  (let [m (str "function(){ 
			  if( this.owner == '"uname"' ) { 
			    this.content[0].content.forEach( 
			        
                    function(x) { 
			          if( x.id == '"currency"' ) { 
			            emit( this.owner , x ); 
			          }
			        }
                );
			  }
			};")
        r   "function(k,vals) { return { result : vals } ; }"
        result {} #_(map-reduce :bookkeeping m r {:inline 1})]

    (if (-> result empty? not)
      (-> result first :value bkell.domain/keywordize-tags) ;; dig in and get the currency
      nil
    )
  )
)


;; get account 
(defn get-accounts [uname] 

  (let[ m (str "function(){ 
			  if( (this.content[1].content != null) && (this.owner == '"uname"') ) { 
			    this.content[1].content.forEach( 
			      function(x) { 
			        emit( this.owner , x ); 
			      }
			    );
			  }
			};")
        r   "function(k,vals) { return { result : vals } ; }"
        ;result {} #_(map-reduce :bookkeeping m r {:inline 1})
        result (mc/map-reduce "bookkeeping" m r nil MapReduceCommand$OutputType/INLINE {})
        converted (from-db-object ^DBObject (.results ^MapReduceOutput result) true)
      ]
    
    ; digging into a structure that looks like this: [{:_id nil, :value {:result [{:counterWeight "debit", :name "cash", :type "asset", :id "cash", :tag "account"} {:counterWeight "credit", :name "expense", :type "expense", :id "expense", :tag "account"} {:counterWeight "debit", :name "revenue", :type "revenue", :id "revenue", :tag "account"} {:counterWeight "credit", :name "accounts payable", :type "liability", :id "accounts payable", :tag "account"}]}}]
    (-> converted first :value :result)
    
    ;;(println (str "get-accounts > result[" (first result) "]"))
    #_(if (empty? result)
      (vec result)
      (vec (map bkell.domain/keywordize-tags 
        (if (-> result first :value :result vector?)
          (-> result first :value :result)
          [(-> result first :value)]
        )
      ))  ;; dig in and get the currency list 
    )
  )
)
(defn get-account [uname account]

  (let[ m (str "function(){ 
			  if( (this.content[1].content != null) && (this.owner == '"uname"') ) { 
			    this.content[1].content.forEach( 
			        
                    function(x) { 
			          if( x.id == '"account"' ) { 
			            emit( this.owner , x ); 
			          }
			        }
                );
			  }
			};")
        r   "function(k,vals) { return { result : vals } ; }"
        result (mc/map-reduce "bookkeeping" m r nil MapReduceCommand$OutputType/INLINE {})
        converted (from-db-object ^DBObject (.results ^MapReduceOutput result) true)
      ]
    
    ; digging into a structure that looks like this: {:_id nil, :value {:counterWeight "debit", :name "cash", :type "asset", :id "cash", :tag "account"}}
    (-> converted first :value)
  )
)



;; get entry 
(defn get-entries [uname] 

  (let [m (str "function(){ 
			  if( (this.content[2].content[0].content[0].content != null) && (this.owner == '"uname"') ) { 
                this.content[2].content[0].content[0].content.forEach(
			      
                  function(x) { 
                    emit( this.owner , x ); 
                  }
                );
			  }
			};" )
        r   "function(k,vals) { return { result : vals } ; }"
        result {} #_(map-reduce :bookkeeping m r {:inline 1})]
    
    (println (str "get-entries: " (pr-str result)))
    
    (if (empty? result)
      (vec result)
      (vec (map bkell.domain/keywordize-tags 
        (if (-> result first :value :result vector?)  ;; deal with a multiple results (list), versus single result (map)
          (-> result first :value :result)
          [(-> result first :value)]
        )
      ))  ;; dig in and get the currency list 
    )
  )
)
(defn get-entry [uname entry]

  (let [m (str "function(){ 
			  if( (this.content[2].content[0].content[0].content != null) && (this.owner == '"uname"') ) { 
			    this.content[2].content[0].content[0].content.forEach(
			        
                    function(x) { 
			          if( x.id == '"entry"' ) { 
			            emit( this.owner , x ); 
			          }
			        }
                );
			  }
			};")
        r   "function(k,vals) { return { result : vals } ; }"
        result {} #_(map-reduce :bookkeeping m r {:inline 1})]

    (if (-> result empty? not)
      (-> result first :value bkell.domain/keywordize-tags)  ;; dig in and get the account
      nil
    )
  )
)



(defmulti get (fn [tagk & etal] tagk))

(defmethod get :user [tagk & etal] (get-user (first etal)))
(defmethod get :group [tagk & etal] (get-group (first etal)))
(defmethod get :bookkeeping [tagk & etal] (get-bookkeeping (first etal)))

(defmethod get :currencies [tagk & etal] (get-currencies (first etal)))
(defmethod get :currency [tagk & etal] (get-currency (first etal) (second etal)))  ;; arguments are: 'uname' 'currency' 

(defmethod get :accounts [tagk & etal] (get-accounts (first etal)))
(defmethod get :account [tagk & etal] (get-account (first etal) (second etal)))  ;; arguments are: 'uname' 'account' 

(defmethod get :entries [tagk & etal] (get-entries (first etal)))
(defmethod get :entry [tagk & etal] (get-entry (first etal) (second etal)))  ;; arguments are: 'uname' 'entry' 



(ns bkell.commands
  
  ;;(:require clojure.contrib.logging)
  (:require clojure.string)
  (:require clojure.pprint)
  (:require [clojure.zip :as zip])
  (:require bkell.domain)
  (:require bkell.util)
  
  (:use somnium.congomongo)
  ;;(:require debug)
)


(defn add-user [user] 
  
  { :pre  [ (bkell.util/verify-arg 
              (not (= (:username user) ;; check that there is not a duplicate user 
                      (:username (first (fetch "users" :where { :username (:username user) })))))
              "This is a duplicate User"
            )
          ]}
  
  (let [gr (load-file "etc/data/default.group.clj")]  ;; insert the associated group
    (insert! :groups (assoc gr :name (:username user) :owner (:username user))))
  (let [bk (load-file "etc/data/default.bookkeeping.clj")]  ;; insert the associated bookkeeping
    (insert! :bookkeeping (assoc bk :owner (:username user))))
  (bkell.domain/keywordize-tags 
    (insert! :users (assoc user :password (bkell.domain/md5-sum (:password user))))) ;; insert the user, after MD5 encrypting the password 
)



(defn add-currency [currency uname default]
  
  { :pre  [ (not (nil? uname)) 
            (not (clojure.string/blank? (:name currency)))
            (not (clojure.string/blank? (:id currency)))
            (= 0 (count (fetch "bookkeeping" :where { "content.content.id" (:id currency) }))) ;; ensure no duplicates 
          ] }
  
  ;; creating a zipper function. Good reference points are: 
  ;;  1. http://tech.puredanger.com/2010/10/22/zippers-with-records-in-clojure 
  ;;  2. http://tech.puredanger.com/2010/10/23/pattern-matching-and-tree-mutation
  (let  [ ru (fetch-one "bookkeeping" :where { :owner uname }) ]
    
    ;;(debug/debug-repl)
    (if-let [result     ;; result will be a 'com.mongodb.WriteResult'
      (update! :bookkeeping { :_id (:_id ru) }  ;; passing in hash w/ ObjecId, NOT original object 
        (bkell.domain/modify-currency                       ;; update the currency if existing  
          ru
          :insert
          currency 
          default))]
      
      (if (-> result .getLastError .ok)
        currency
        (bkell.util/generate-error-response (.getErrorMessage result)))
    )
  )
)


(defn add-account 
  " 1. account types are: asset, liability, expense, revenue
    2. each account has a given counter weight
   
    type='asset'       counterWeight='debit'
    type='expense'     counterWeight='debit'
    type='liability'   counterWeight='credit'
    type='revenue'     counterWeight='credit'
  "
  [account uname] 
  
  { :pre  [ (not (nil? uname))
            (not (nil? account))
            (not (clojure.string/blank? (:name account)))
            (not (clojure.string/blank? (:id account)))
            (not (nil? (:type account)))
            (not (nil? (:counterWeight account)))
            (= 0 (count (fetch "bookkeeping" :where { "content.content.id" (:id account) }))) ;; ensure no duplicates 
          ] }
  
  (let  [ ru (fetch-one "bookkeeping" :where { :owner uname }) ]
    
    (if-let [result ;; result will be a 'com.mongodb.WriteResult' 
      (update! :bookkeeping { :_id (:_id ru) }  ;; passing in hash w/ ObjecId, NOT original object
        (bkell.domain/traverse-tree ru :insert { :id "main.accounts" } account))]
      
      (if (-> result .getLastError .ok)
        account
        (bkell.util/generate-error-response (.getErrorMessage result)))
    )
  )
)


(defn add-entry [entry uname]
  
  { :pre  [ (not (nil? uname))
            (not (nil? entry))
            (not (clojure.string/blank? (:id entry)))
            (not (clojure.string/blank? (:date entry)))
            
            ;; ASSERT that accounts correspond with existing accounts
            (bkell.domain/account-for-entry? uname entry)
            
            
            ;; ASSERT that entry is balanced 
            ;; :lhs -> dt/dt == ct/ct
            ;; :rhs -> dt/cr == ct/dt 
            (bkell.domain/entry-balanced? uname entry)
          ]
  }
  
  (let  [ ru (fetch-one "bookkeeping" :where { :owner uname }) ]
    
    (if-let [result ;; result will be a 'com.mongodb.WriteResult' 
      (update! :bookkeeping { :_id (:_id ru) }  ;; passing in hash w/ ObjecId, NOT original object
        (bkell.domain/traverse-tree ru :insert { :id "main.entries" } entry))]
      
      (if (-> result .getLastError .ok)
        entry
        (bkell.util/generate-error-response (.getErrorMessage result)))
    )
  )
)


(defmulti add (fn [obj & etal] (:tag obj)))
(defmethod add :user [user] (add-user user))
(defmethod add :currency [currency & etal] (add-currency currency (first etal) (second etal)))   ;; input arguments are: currency uname default
(defmethod add :account [account & etal] (add-account account (first etal)))  ;; input arguments are: account uname 
(defmethod add :entry [entry & etal] (add-entry entry (first etal)))  ;; input arguments are: entry uname 


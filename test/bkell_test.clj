(ns bkell-test 
  (:use [bkell] :reload-all)
  (:use [clojure.test])
  (:require [test-utils]
            [clojure.contrib.logging]
            [domain])
)


(use-fixtures :each test-utils/test-fixture-db)
(somnium.congomongo/mongo! :db "bkell") ;; connect to mongodb


;; ==================
;; 'ADD' tests
;; ==================
(deftest test-addU
  (let [  user (load-file "test/etc/data/stubu-two.clj")
          bk (bkell/init-shell)      ;; initialize the bkell 
          ruser (bkell/add user)]

    (is (= :user (:tag ruser)))  ;; ensure that a :user tag is coming back 
  )
)
(deftest test-addC
  (let [user (load-file "test/etc/data/stubu-two.clj")
        ru (commands/add-user user)
        
        bk (bkell/init-shell)      ;; initialize the bkell 
        currency (load-file "test/etc/data/test-currency.clj")]
    
    
    ;; ensure that an error is returned if we try to add a currency without logging in 
    (let [ eresult (bkell/add currency "stub" false)]
      (is (-> eresult nil? not))
      (is (-> eresult :tag (= :error)))
    )
    
    ;; now log-in a user
    (commands/login-user ru) 
    
    (let [ fresult (bkell/add currency "stub" false)]
      
      (is (-> fresult nil? not))
      (is (-> fresult :tag (= :currency)))
    )
  )
)
(deftest test-addA
  (let [user (load-file "test/etc/data/stubu-two.clj")
        ru (commands/add-user user)
        
        bk (bkell/init-shell)      ;; initialize the bkell 
        account (load-file "test/etc/data/test-account-asset.clj")]
    
    ;; ensure that an error is returned if we try to add an account without logging in 
    (let [ eresult (bkell/add account "stub" false)]
      (is (-> eresult nil? not))
      (is (-> eresult :tag (= :error)))
    )
    ;; now log-in a user
    (commands/login-user ru) 
    
    (let [fresult (bkell/add account "stub")]
      
      (is (-> fresult nil? not))
      (is (-> fresult :tag (= :account)))
    )
  )
)
(deftest test-addE
  (let  [ user (load-file "test/etc/data/stubu-two.clj")
          ru (commands/add-user user)
          
          bk (bkell/init-shell)      ;; initialize the bkell 
          xx (test-utils/populate-accounts)
          entry (test-utils/create-balanced-test-entry)
         ]
    
    ;; ensure that an error is returned if we try to add an entry without logging in 
    (let [ eresult (bkell/add entry "stub" false)]
      (is (-> eresult nil? not))
      (is (-> eresult :tag (= :error)))
    )
    ;; now log-in a user
    (commands/login-user ru) 
    
    (let [fresult (bkell/add entry "stub")]
      
      (is (-> fresult nil? not))
      (is (-> fresult :tag (= :entry)))
    )
  )
)

;; ==================
;; 'GET' tests
;; ==================
(deftest test-getU
  (let [  user (load-file "test/etc/data/stubu-two.clj")
          bk (bkell/init-shell)      ;; initialize the bkell 
          ruser (bkell/add user)]
    
    ;; ensure that an error is returned if we try to get a user without logging in 
    (let [ eresult (bkell/get :user (:username ruser))]
      (is (-> eresult nil? not))
      (is (-> eresult :tag (= :error)))
    )
    
    ;; now log-in a user
    (commands/login-user ruser) 
    
    (let [ fresult (bkell/get :user (:username ruser))]
      
      (println "... " fresult)
      (is (-> fresult nil? not))
      (is (-> fresult :tag (= :user)))
    )
    
  )
)
(deftest test-getC
  (let [user (load-file "test/etc/data/stubu-two.clj")
        ru (commands/add-user user)
        
        bk (bkell/init-shell)      ;; initialize the bkell 
        currency (load-file "test/etc/data/test-currency.clj")]
    
    
    ;; ensure that an error is returned if we try to add a currency without logging in 
    (let [ eresult (bkell/add currency "stub" false)]
      (is (-> eresult nil? not))
      (is (-> eresult :tag (= :error)))
    )
    
    ;; now log-in a user
    (commands/login-user ru) 
    
    (let [ fresult (bkell/add currency "stub" false)]
      
      (is (-> fresult nil? not))
      (is (-> fresult :tag (= :currency)))
    )
    
  )
)


;; ==================
;; 'UPDATE' tests
;; ==================


;; ==================
;; 'REMOVE' tests
;; ==================





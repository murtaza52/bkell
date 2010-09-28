(ns login-test

	(:use [helpers] :reload-all)
	(:use [clojure.test])
	(:use [depth_adapter])
	(:require [bkell])

	(:import java.io.ByteArrayInputStream)
	(:require clojure.contrib.str-utils)
    (:require commands.add)
)


(def configs (load-file "etc/config/config.test.clj"))


;; FIXTUREs
(defn test-fixture-shell
    "Initialize the shell"
    [test]

    (bkell/init-shell)
)
(defn test-fixture-db
    "test to clear out shell memory before a test is run"
    [test]

    ;; make the shell active
	(dosync
		(alter bkell/shell conj
			{ :active true }))

    ;; create a basic user in the DB
    (add-user (:url-test configs) (:system-dir configs) { :tag "user" :attrs { :id "test.user" } } )

    ;; ** execute the TEST function
    (test)

    ;; make the shell inactive
	(dosync
		(alter bkell/shell conj
			{ :active false }))

)

(use-fixtures :once test-fixture-shell)
(use-fixtures :each test-fixture-db)

;; test basic login
(deftest test-login []

    (is (= 5 5))
)


;; test result when already logged in
(deftest test-existing-login []

)


;; test a login with a bad password
(deftest test-bad-password []

)


;; test logging out
(deftest test-logout []

)



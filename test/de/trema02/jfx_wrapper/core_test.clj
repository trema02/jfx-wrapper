(ns de.trema02.jfx-wrapper.core-test
  (:require [clojure.test :refer :all]
            [de.trema02.jfx-wrapper.core :as c]))

(deftest explain-app-data-error-test
  (testing "explain-app-data-error"
    (let [gui (de.trema02.jfx_wrapper.core.GUI.)
          data (c/get-app-data)]
      (is (= "Not a map." (c/explain-app-data-error gui data :map)))
      (is (= "Undefined keys: #{:title}." (c/explain-app-data-error gui (dissoc data :title) :all-keys)))
      (is (= ":config-fn must be nil or a function." (c/explain-app-data-error gui data :config-fn)))
      (is (= ":title must be a string." (c/explain-app-data-error gui data :title)))
      (is (= ":mk-root-fn must be nil or a function." (c/explain-app-data-error gui data :mk-root-fn)))
      (is (= ":css-name must be nil or a valid resource." (c/explain-app-data-error gui data :css-name)))
      (is (= ":x-size must be a positive integer." (c/explain-app-data-error gui data :x-size)))
      (is (= ":y-size must be a positive integer." (c/explain-app-data-error gui data :y-size)))
      (is (= ":image-name must be nil or a valid resource." (c/explain-app-data-error gui data :image-name)))
      (is (= ":fxml-name must be nil or a valid resource." (c/explain-app-data-error gui data :fxml-name)))
      (is (= ":end-fn must be nil or a function." (c/explain-app-data-error gui data :end-fn)))
      (is (= "OK." (c/explain-app-data-error gui data :ok))))))

(deftest get-app-data-test
  (testing "get-app-data"
    (is (= {:end-fn nil, :config-fn nil, :title "", :mk-root-fn nil, :css-name nil, :y-size 480, :image-name nil, :x-size 640, :fxml-name nil}
           (c/get-app-data)))))

(deftest app-data-valid?-test
  (testing "app-data-valid?"
    (let [gui (de.trema02.jfx_wrapper.core.GUI.)
          data (c/get-app-data)]
      (is (= :map (c/app-data-valid? gui 0)))
      (is (= :all-keys (c/app-data-valid? gui (dissoc data :title))))
      (is (= :config-fn (c/app-data-valid? gui (assoc data :config-fn 0))))
      (is (= :title (c/app-data-valid? gui (assoc data :title 0))))
      (is (= :mk-root-fn (c/app-data-valid? gui (assoc data :mk-root-fn 0))))
      (is (= :css-name (c/app-data-valid? gui (assoc data :css-name 0))))
      (is (= :x-size (c/app-data-valid? gui (assoc data :x-size -1))))
      (is (= :y-size (c/app-data-valid? gui (assoc data :y-size -1))))
      (is (= :image-name (c/app-data-valid? gui (assoc data :image-name 0))))
      (is (= :fxml-name (c/app-data-valid? gui (assoc data :fxml-name 0))))
      (is (= :end-fn (c/app-data-valid? gui (assoc data :end-fn 0))))
      (is (= :ok (c/app-data-valid? gui data))))))

;(run-tests)

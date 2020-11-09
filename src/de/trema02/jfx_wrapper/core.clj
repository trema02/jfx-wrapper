(ns de.trema02.jfx-wrapper.core
  (:gen-class)
  (:import [javafx.application Application Platform]
           [javafx.stage Stage]
           [javafx.fxml FXMLLoader]
           [javafx.event EventHandler]
           [javafx.scene Scene]
           [javafx.scene.image Image])
  (:require [clojure.set :as st]))

(gen-class
   :name de.trema02.jfx_wrapper.core.GUI
   :extends javafx.application.Application
   :main false
   :exposes-methods {stop stop_gui}
   :methods [[entry [] void]]) ;"abstract methods work automagically"


(def mc (atom nil))
(def gui-root (atom nil))
(def gui (atom nil))
(def style-sheet (atom nil))
(def image (atom nil))
(def ^{:private false} primary-stage (atom nil))
(def data (atom {:x-size 640 :y-size 480 :fxml-name nil :css-name nil
                 :image-name nil :config-fn nil :title "" :mk-root-fn nil
                 :end-fn nil :debug false}))
(def all-keys '(:config-fn :title :mk-root-fn :css-name :x-size :y-size
                :image-name :fxml-name :end-fn :debug))

(defn app-data-valid? [app mp]
  (let [result (atom :map)]
    (and
     (map? mp)
     (do (reset! result :all-keys) true)
     (every? #(contains? mp %)
             all-keys)
     (do (reset! result :config-fn) true)
     (let [val (:config-fn mp)]
       (or (nil? val) (fn? val)))
     (do (reset! result :title) true)
     (let [val (:title mp)]
       (string? val))
     (do (reset! result :mk-root-fn) true)
     (let [val (:mk-root-fn mp)]
       (or (nil? val) (fn? val)))
     (do (reset! result :css-name) true)
     (let [val (:css-name mp)]
       (or (nil? val) (and (string? val) (.. app getClass (getResource val)))))
     (do (reset! result :x-size) true)
     (let [val (:x-size mp)]
       (pos? val))
     (do (reset! result :y-size) true)
     (let [val (:y-size mp)]
       (pos? val))
     (do (reset! result :image-name) true)
     (let [val (:image-name mp)]
       (or (nil? val) (and (string? val) (.. app getClass (getResource val)))))
     (do (reset! result :fxml-name) true)
     (let [val (:fxml-name mp)]
       (or (nil? val) (and (string? val) (.. app getClass (getResource val)))))
     (do (reset! result :end-fn) true)
     (let [val (:end-fn mp)]
       (or (nil? val) (fn? val)))
     (do (reset! result :ok) true))
    @result))

(defn explain-app-data-error [_ mp key]
  (case key
    :map (str "Not a map.")
    :all-keys (let [all-key-set (into #{} all-keys)
                    mp-key-set (into #{} (keys mp))]
                (str "Undefined keys: "
                     (st/difference all-key-set mp-key-set)
                     "."))
    :config-fn (str ":config-fn must be nil or a function.")
    :title (str ":title must be a string.")
    :mk-root-fn (str ":mk-root-fn must be nil or a function.")
    :css-name (str ":css-name must be nil or a valid resource.")
    :x-size (str ":x-size must be a positive integer.")
    :y-size (str ":y-size must be a positive integer.")
    :image-name (str ":image-name must be nil or a valid resource.")
    :fxml-name (str ":fxml-name must be nil or a valid resource.")
    :end-fn (str ":end-fn must be nil or a function.")
    :ok (str "OK.")))

(defn set-app-data [app mp]
  (let [result-key (app-data-valid? app mp)
        ok (= result-key :ok)]
    (if ok
      (reset! data mp)
      (throw (Exception. (str "jfx_wrapper.core/set-app-data: " (explain-app-data-error app mp result-key)))))))

(defn get-app-data []
  @data)

(defn event-handler [f & args]
  (proxy [EventHandler] []
    (handle [event] (apply f event args))))

(declare start-stage)
(def close-handler (event-handler (fn [_]
                                    (if (:debug @data)
                                      (Platform/runLater #(start-stage))
                                      (Platform/exit)))))

(defn start-stage []
  (let [{:keys [fxml-name mk-root-fn]} @data]
    (cond
      fxml-name (let [res (.. @gui getClass (getResource fxml-name))
                      loader (FXMLLoader. res)
                      r (.load loader)
                      main-controller (.getController loader)]
                  (reset! gui-root r)
                  (reset! mc main-controller))
      mk-root-fn (let [r (mk-root-fn)]
                   (reset! gui-root r))
      :else (throw (Exception. (str "start: :fxml-name or :mk-root-fn must be specified."))))
    (let [css (:css-name @data)]
      (if css
        (reset! style-sheet (.. @gui getClass (getResource css) toExternalForm))
        nil))
    (let [img (:image-name @data)]
      (if img
        (do
          (reset! image (Image. img))
          (doto (.getIcons @primary-stage) (.add @image)))
        nil))
    (let [cf (:config-fn @data)]
      (if cf
        (cf @gui-root @mc)
        nil))
    (let [scene (Scene. @gui-root (:x-size @data) (:y-size @data))
          stage (Stage.)]
      (if @style-sheet
        (.. @scene getStylesheets (add @style-sheet))
        nil)
      (.setTitle stage (:title @data))
      (doto stage
        (.setScene scene)
        (.setOnCloseRequest (or (:end-fn @data) close-handler))
        (.show)))))

(defn -start [this stage]
  (reset! primary-stage stage)
  (reset! gui this)
  (Platform/setImplicitExit false)
  (Platform/runLater #(start-stage))
  nil)

(defn- -entry [this]
  (Application/launch (.getClass this) (make-array java.lang.String 0)))

(defn -stop [this]
  (println "STOP")
  (.stop_gui this))

(comment defn -main [& _]
  (.entry (de.trema02.jfx_wrapper.core.GUI.)))

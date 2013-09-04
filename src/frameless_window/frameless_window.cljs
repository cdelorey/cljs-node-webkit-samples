(ns cljs-node-webkit-examples.frameless-window
  (:use [cljs-node-webkit-examples.titlebar :only [focus-titlebars update-content-style
                                                   add-titlebar remove-titlebar]]
        [domina :only [by-id]]
        [domina.events :only [listen!]]))

(defn reset-checkboxes [boxes]
  (doall 
    (map #(when (aget % "disabled") (aset % "disabled" false)) boxes)))

(defn update-checkbox []
  (let [top-checkbox (by-id "top-box")
    bottom-checkbox (by-id "bottom-box")
    left-checkbox (by-id "left-box")
    right-checkbox (by-id "right-box")]
    (cond
      (or (aget bottom-checkbox "checked") (aget top-checkbox "checked"))
      (do
        (aset right-checkbox "disabled" true)
        (aset left-checkbox "disabled" true))
      (or (aget right-checkbox "checked") (aget left-checkbox "checked"))
      (do
        (aset top-checkbox "disabled" true)
        (aset bottom-checkbox "disabled" true))
      :else (reset-checkboxes [top-checkbox bottom-checkbox 
                               left-checkbox right-checkbox]))))

(defn init-checkbox
  [checkbox-id titlebar-name titlebar-icon-url titlebar-text]

  (let [elem (by-id checkbox-id)]
    (when elem
      (listen! elem :click 
            #(do
               (if (.-checked (by-id checkbox-id))
                (add-titlebar titlebar-name titlebar-icon-url titlebar-text)
                (remove-titlebar titlebar-name titlebar-icon-url titlebar-text))
               (focus-titlebars true)
               (update-content-style)
               (update-checkbox))))))

(defn init []
  (do
    (init-checkbox "top-box" "top-titlebar" "top-titlebar.png" "Top Titlebar")
    (init-checkbox "bottom-box" "bottom-titlebar" "bottom-titlebar.png" "Bottom Titlebar")
    (init-checkbox "left-box" "left-titlebar" "left-titlebar.png" "Left Titlebar")
    (init-checkbox "right-box" "right-titlebar" "right-titlebar.png" "Right Titlebar")
    
    (listen! (by-id "close-window-button") :click #(.close js/window))
    (update-content-style)
    
    (.show (.get (.-Window (js/require "nw.gui"))))))

;; set handlers
(listen! js/window :focus #(do
                             (.log js/console "focus")
                             (focus-titlebars true)))
(listen! js/window :blur #(do
                            (.log js/console "blur")
                            (focus-titlebars false)))
(listen! js/window :resize update-content-style)
(listen! js/window :load init)
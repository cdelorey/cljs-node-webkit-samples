(ns cljs-node-webkit-examples.frameless-window
  (:use [cljs-node-webkit-examples.titlebar :only [focus-titlebars update-content-style
                                                   add-titlebar remove-titlebar]]))

(defn reset-checkboxes [boxes]
  (doall 
    (map #(when (aget % "disabled") (aset % "disabled" false)) boxes)))

(defn update-checkbox []
  (let [top-checkbox (.getElementById js/document "top-box")
    bottom-checkbox (.getElementById js/document "bottom-box")
    left-checkbox (.getElementById js/document "left-box")
    right-checkbox (.getElementById js/document "right-box")]
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

  (let [elem (.getElementById js/document checkbox-id)]
    (when elem
      (set! (.-onclick elem) 
            #(do
               (if (.-checked (.getElementById js/document checkbox-id))
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
    
    (set! (.-onclick (.getElementById js/document "close-window-button")) 
          #(.close js/window))
    (update-content-style)
    
    (.show (.get (.-Window (js/require "nw.gui"))))))

;; set handlers
(set! (.-onfocus js/window) #(do
                               (.log js/console "focus")
                               (focus-titlebars true)))

(set! (.-onblur js/window) #(do
                              (.log js/console "blur")
                              (focus-titlebars false)))

(set! (.-onresize js/window) update-content-style)

(set! (.-onload js/window) init)
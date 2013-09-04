(ns cljs-node-webkit-examples.titlebar
  (:use [domina :only [by-id append! set-attr! detach!]]))

(defn close-window []
  (.close js/window))

(defn update-image-url
  [image-id image-url]
  (if-let [image (by-id "image_id")]
    (aset image "src" image-url)))

(defn create-image
  [image-id image-url]
  (let [image (.createElement js/document "img")]
    (doto image
      (set-attr! "id" image-id)
      (aset "src" image-url))))

(defn create-button
  [button-id button-name normal-image-url hover-image-url click-func]
  (let [button (.createElement js/document "div")
        button-img (create-image button-id normal-image-url)]
    (doto button
      (set-attr! "class" button-name)
      (append! button-img)
      (aset "onmouseover" #(update-image-url button-id hover-image-url))
      (aset "onmouseout" #(update-image-url button-id normal-image-url))
      (aset "onclick" click-func))))

(defn focus-titlebars
  [focus]
  (let [bg-color (if focus "#3a3d3d" "#7a7c7c")
        titlebars (filter identity (map #(by-id %)
                       ["top-titlebar" "bottom-titlebar" "left-titlebar" "right-titlebar"]))]
    (doall
      (map #(set! (.-backgroundColor (.-style %)) bg-color) titlebars))))

(defn remove-titlebar
  [titlebar-name]
  (if-let [titlebar (by-id titlebar-name)]
    (detach! titlebar)))


;; add-titlebar functions -----------------------------------------------------
(defn create-icon
  [titlebar-name titlebar-icon-url]
  (doto (.createElement js/document "div")
    (set-attr! "class" (str titlebar-name "-icon"))
    (append! (create-image (str titlebar-name + "icon") titlebar-icon-url))))

(defn create-title
  [titlebar-name titlebar-text]
  (doto (.createElement js/document "div")
    (set-attr! "class" (str titlebar-name "-text"))
    (aset "innerText" titlebar-text)))

(defn create-divider
  [titlebar-name]
  (doto (.createElement js/document "div")
    (set-attr! "class" (str titlebar-name + "-divider"))))

(defn create-close-button
  [titlebar-name]
  (create-button (str titlebar-name "-close-button")
                 (str titlebar-name "-close-button")
                 "button_close.png" "button_close_hover.png" close-window))

(defn add-titlebar
  [titlebar-name titlebar-icon-url titlebar-text]
  (let [titlebar (.createElement js/document "div")
    icon (create-icon titlebar-name titlebar-icon-url)
    title (create-title titlebar-name titlebar-text)
    divider (create-divider titlebar-name)
    close-button (create-close-button titlebar-name)]
    (append! (aget js/document "body")
      (doto titlebar
        (set-attr! "id" titlebar-name)
        (set-attr! "class" titlebar-name)
        (append! icon)
        (append! title)
        (append! divider)
        (append! close-button)))))


;; update-content-style functions ---------------------------------------------
(defn calculate-height []
  (let [height (.-outerHeight js/window)
        top (by-id "top-titlebar")
        bottom (by-id "bottom-titlebar")]
    (cond
      (and bottom top) (- height (+ (.-offsetHeight top) (.-offsetHeight bottom)))
      bottom (- height (.-offsetHeight bottom))
      top (- height (.-offsetHeight top))
      :else height)))

(defn calculate-width []
  (let [width (.-outerWidth js/window)
        right (by-id "right-titlebar")
        left (by-id "left-titlebar")]
    (cond
      (and right left) (- width (+ (.-offsetWidth right) (.-offsetWidth left)))
      right (- width (.-offsetWidth right))
      left (- width (.-offsetWidth left))
      :else width)))

(defn calculate-left []
  (if-let [left-titlebar (by-id "left-titlebar")]
    (.-offsetWidth left-titlebar)
    0))

(defn calculate-top []
  (if-let [top-titlebar (by-id "top-titlebar")]
    (.-offsetHeight top-titlebar)
    0))

(defn update-content-style []
  (if-let [content (by-id "content")]
    (let [left (calculate-left)
          top (calculate-top)
          width (calculate-width)
          height (calculate-height)
          content-style (str "position: absolute; " "left: " left "px; " 
                             "top: " top "px; " "width: " width "px; " 
                             "height: " height "px; " )]
      (set-attr! content "style" content-style))))

// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const DEFAULT_CENTER = {lat: 42.2831, lng: -87.9531};
const DEFAULT_ZOOM = 16;
const FOOD_LOCATION = {lat: 42.2861222, lng:-87.9547825};
const HS_LOCATION = {lat: 42.2846165, lng: -87.9668647};
const LIBERTY_LOCATION = {lat: 42.2897481, lng: -87.9544839};
const LOCATION_MAP = new Map([
    [FOOD_LOCATION, "My favorite restaurant."],
    [HS_LOCATION, "My old high school."],
    [LIBERTY_LOCATION, "The old theater I used to work at!"]
]);


/**
 * Calls initial functions on load   
 */
function initialize() {
  getComments();
  showMap();  
}

/**
 * Advances image gallery to next image.
 */
function nextImage() {
  const images =
      ['/images/cssi.png', '/images/dogs.png', '/images/gc.png', '/images/lib.png'];

  // Choose a random image in the array.
  const currentImage = images[Math.floor(Math.random() * images.length)];

  // Add it to the page.
  const imageContainer = document.getElementById('image');
  imageContainer.src = currentImage;
  
}

/**
 * Creates and displays Google map
 */
function showMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: DEFAULT_CENTER, zoom: DEFAULT_ZOOM});
  
  for (let [location,info] of LOCATION_MAP) {
    addMarkerInfo(map, location, info);
  };     
}

/**
 * Add marker to the map along with info window on click
 */
function addMarkerInfo(currentMap, coordinate, info) {
  var marker = new google.maps.Marker({position: coordinate, map: currentMap});

  var infowindow = new google.maps.InfoWindow({
    content: info
  });

  marker.addListener('click', function() {
    infowindow.open(currentMap, marker);
  }); 
}

/**
 * Fetches data and adds to html
 */
function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
      const msgContainer = document.getElementById('hello-container');
      msgContainer.innerHTML = "";

      comments.forEach(function(comment) {
        msgContainer.innerHTML += "</br>" + comment.content + " Sentiment Score: " + comment.sentimentScore;
      });

    });
}

/**
 * Deletes data
 */
function deleteComments() {
  fetch('/delete-data', {method: 'POST', body: ""}).then(getComments());
}

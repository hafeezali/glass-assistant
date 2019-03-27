import sys
import requests
import json
# If you are using a Jupyter notebook, uncomment the following line.
#%matplotlib inline
import matplotlib.pyplot as plt
from PIL import Image
from io import BytesIO

# Replace <Subscription Key> with your valid subscription key.
subscription_key = "d8e8472ab0ee4726ab56f1e9fa79ad13"
assert subscription_key

# You must use the same region in your REST call as you used to get your
# subscription keys. For example, if you got your subscription keys from
# westus, replace "westcentralus" in the URI below with "westus".
#
# Free trial subscription keys are generated in the "westus" region.
# If you use a free trial subscription key, you shouldn't need to change
# this region.
vision_base_url = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/"

# analyze_url = vision_base_url + "describe"

analyze_url = vision_base_url + sys.argv[2]

# Set image_path to the local path of an image that you want to analyze.
# image_path = "./../../ImagesReceived/5.jpg"

# Uncomment this and comment the above line while using the glass app.
image_path = sys.argv[1]

# Read the image into a byte array
image_data = open(image_path, "rb").read()
headers    = {'Ocp-Apim-Subscription-Key': subscription_key,
              'Content-Type': 'application/octet-stream'}
params     = {'visualFeatures': 'Categories,Description,Color'}
response = requests.post(
    analyze_url, headers=headers, params=params, data=image_data)
response.raise_for_status()

# The 'analysis' object contains various fields that describe the image. The most
# relevant caption for the image is obtained from the 'description' property.
analysis = response.json()
# print(analysis)

if sys.argv[2] == "describe":
	captionJSON = analysis['description']['captions'][0]
	caption = captionJSON['text']
	caption = "I think it's " + caption
	print(caption)

if sys.argv[2] == "detect":
	objects = ""
	objectsJSONList = analysis['objects']
	objectsList = []
	for objectJSON in objectsJSONList:
		objectsList.append(objectJSON['object'])
	if len(objectsList) > 2:
		for i in range(len(objectsList)-2):
			objects = objects + objectsList[i] + " , "
		objects = objects + objectsList[-2] + " and " + objectsList[-1] + "."
		objects = "The objects in the scene are " + objects
	elif len(objectsList) == 2:
		objects = objectsList[0] + " and " + objectsList[1] + "."
		objects = "The objects in the scene are " + objects
	else:
		objects = objectsList[0] + "."
		objects = "The object in the scene is a " + objects
	print(objects)


# Not required for glass application
# image_caption = analysis["description"]["captions"][0]["text"].capitalize()

# Display the image and overlay it with the caption.
# Not required for glass app
# image = Image.open(BytesIO(image_data))
# plt.imshow(image)
# plt.axis("off")
# _ = plt.title(image_caption, size="x-large", y=-0.1)
rm splitdoc/*.txt
java -jar PreProcessing.jar
rm -r index
java -jar CreateIndex.jar
java -jar NewSearchFile.jar
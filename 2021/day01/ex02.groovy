def fileContentAsArray = []
new File('puzzle.txt').eachLine {fileContentAsArray.add(Integer.parseInt(it.trim()))}
def prev = null
def measurements = []
def triples = []
for (i in (0..<fileContentAsArray.size())) {
    if(i + 3 <= fileContentAsArray.size()) {
        def tempTripel = []
        for(j in (0..2)) {
            tempTripel.add(fileContentAsArray.get(i + j))
        }
        triples.add(tempTripel)
    }
}
triples.collect{it.sum()}.each{
    if(prev == null) {
       measurements.add("${it} (N/A - no previous measurement)")
    } else if (prev < it){
        measurements.add("${it} (increased)")
    } else {
        measurements.add("${it} (decreased)")
    }
    prev = it
}
println("How many measurements are larger than the previous measurement? " + measurements.findAll{ it.contains('increased')}.size())
return 0


def fileContentAsArray = []
new File('puzzle.txt').eachLine {fileContentAsArray.add(Integer.parseInt(it.trim()))}
def prev = null
def measurements = []
fileContentAsArray.each{
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


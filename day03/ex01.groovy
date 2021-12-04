def fileContentAsString = ''
def file = new File('puzzle.txt').eachLine {
    fileContentAsString = fileContentAsString + it.trim() + String.format('%n')
}
def fileContentAsArray = fileContentAsString.split('(\r\n|\r|\n)') //line break depends on OS
def rows = fileContentAsArray[0].size()
def columns = fileContentAsArray.size()

println("rows= ${rows}, columns= ${columns}")//\n${fileContentAsArray}")
def gamma = new int[rows]
def epsilon = new int[rows] 
for(i in (0..<rows)){
    def column = '['
    for(j in (0..<columns)) {
        column += fileContentAsArray[j][i] + ','
    }
    column += ']'
    column= Eval.me(column)
    //println("#0=${column.count(0)}, #1=${column.count(1)}")
    gamma[i] = (column.count(0) > column.count(1) ? 0 : 1)
    epsilon[i] = (column.count(0) < column.count(1) ? 0 : 1)
}
println("gamma= ${gamma}\nepsilon= ${epsilon}")
def powerConsumption =  Integer.parseInt(gamma.join(''), 2) * Integer.parseInt(epsilon.join(''), 2)
println("powerConsumption= ${powerConsumption}")
return 0
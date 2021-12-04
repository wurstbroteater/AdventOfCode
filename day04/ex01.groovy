def fileContentAsArray = []
def order = []
def lastLine = null
def boards = []
def tempBoard = []
new File('sample.txt').eachLine { it, index ->
    def line = it.trim()
    //println("${index}: ${it}")
    if (index <= 1) {
        //it inside collect is collect iterator and not from eachLine
       order = it.split(',').collect{Integer.parseInt(it)}
       //println("order= ${order}")
    } else if(index == 2) {
        // skip
    }
    else if (line.isEmpty()){
        //println("board= ${tempBoard}")
        boards.add(tempBoard)
        tempBoard = []
    } else {
        tempBoard.add(line.split(' ').findAll{!it.isEmpty()}.collect{Integer.parseInt(it)})
    }
    lastLine = line
}
//println("board= ${tempBoard}")
boards.add(tempBoard)
def initialBoards = boards
println('start') 
order.each{ n ->
    boards = boards.collect{board -> board.collect{row -> row.findAll{it != n}}}
    try {
        boards.eachWithIndex{board, boradIndex -> board.eachWithIndex{row,rowIndex -> 
            if(row.isEmpty()) {
                //can not break each closure, so throw error instead
                throw new Exception("we won ${boradIndex}, ${rowIndex}")
            }
        }}
    } catch (error){
        def eMatcher = "${error}" =~ /.* we won (?<boradIndex>[0-9]+), (?<rowIndex>[0-9]+).*/
        if(eMatcher.matches()) {
            def boradIndex = Integer.parseInt(eMatcher.group('boradIndex'))
            def rowIndex = Integer.parseInt(eMatcher.group('rowIndex'))
            def winningBoard = boards[boradIndex]
            def sumUnmarked = 0
            winningBoard.findAll{!it.isEmpty()}.each{sumUnmarked += it.sum()}
            throw new Exception("sumUnmarked= ${sumUnmarked}, final score= ${sumUnmarked * n}")
        } else {
             throw error
        }
    }
    //println("boards= ${boards}")
}
return 0

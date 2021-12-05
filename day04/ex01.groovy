(order, boards) = parseFile('sample.txt')
for (i in order) {
    boards = boards.collect { b -> b.collect { r -> r.collect { [it[0], (it[0] == i ? true : it[1])] } } }
    boards_T = boards.collect { b -> b.transpose() }
    def winningBoardIndex = boards.findIndexOf { b -> b.any { r -> r.every { it[1] } } } //rows
    if (winningBoardIndex != -1) {
        println "score=${getSumOfUnmarked(boards[winningBoardIndex]) * i}\n"
        break
    }
}
/*
def mtrx1 =[ 
    [[1,false],[2,true],[3,false]],
    [[4,false],[5,false],[6,false]],
    [[7,false],[8,true],[9,true]]
]
def mtrx2 =[ 
    [[1,false],[2,true],[3,false]],
    [[4,false],[12,false],[6,false]],
    [[11,false],[8,true],[9,true]]
]
//println("row bingo? ${mtrx.any{row -> row.every{it[1]}}}")
//println("column bingo? ${mtrx.transpose().any{row -> row.every{it[1]}}}")
//def mtrxs = [mtrx1, mtrx1.transpose()]
def mtrxs = [mtrx1, mtrx2]
def n = 12
mtrxs = mtrxs.collect{m -> m.collect{row -> row.collect{item -> [item[0],(item[0] == n ? true : item[1])]}}}
mtrxs.each {m -> m.each{r -> r.each{i -> print "${i[0]},${i[1]?'T':'F'} "} println ""} println ""}
println("row bingo? ${mtrxs.any{m -> m.any{row -> row.every{it[1]}}}}")
println("row bingo matrix index: ${mtrxs.findIndexOf{m -> m.any{r -> r.every{it[1]}}}}")
mtrxs_T = mtrxs.collect{m -> m.transpose()}
println("column bingo? ${mtrxs_T.any{m -> m.any{row -> row.every{it[1]}}}}")
println("column bingo matrix index: ${mtrxs_T.findIndexOf{m -> m.any{r -> r.every{it[1]}}}}")
*/
return 0

def getSumOfUnmarked(board) {
    def sum = 0
    board.each { r -> r.each { sum += (!it[1] ? it[0] : 0) } }
    return sum
}

def parseFile(file) {
    def fileContentAsArray = []
    def order = []
    def lastLine = null
    def boards = []
    def tempBoard = []
    new File(file).eachLine { it, index ->
        def line = it.trim()
        //println("${index}: ${it}")
        if (index <= 1) {
            //it inside collect is collect iterator and not from eachLine
            order = it.split(',').collect { Integer.parseInt(it) }
            //println("order= ${order}")
        } else if (index == 2) {
            // skip
        } else if (line.isEmpty()) {
            //println("board= ${tempBoard}")
            boards.add(tempBoard)
            tempBoard = []
        } else {
            tempBoard.add(line.split(' ').findAll { !it.isEmpty() }.collect { [Integer.parseInt(it), false] })
        }
        lastLine = line
    }
    //println("board= ${tempBoard}")
    boards.add(tempBoard)
    return [order, boards]
}

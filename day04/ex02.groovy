(order, boards) = parseFile('puzzle.txt')
def winningBoards = []
def wins = []
for (i in order) {
    boards = boards.collect { b -> b.collect { r -> r.collect { [it[0], (it[0] == i ? true : it[1])] } } }
    boards_T = boards.collect { b -> b.transpose() }
    def winningBoardIndex = boards.findIndexOf { b -> b.any { r -> r.every { it[1] } } } //rows
    if (winningBoardIndex != -1) {
        winningBoards.add(boards[winningBoardIndex])
        boards.remove(winningBoardIndex)
        boards_T = boards.collect { b -> b.transpose() }
        wins.add(i)
    }
    winningBoardIndex = boards_T.findIndexOf { b -> b.any { r -> r.every { it[1] } } } //columns
    if (winningBoardIndex != -1) {
        winningBoards.add(boards[winningBoardIndex])
        boards.remove(winningBoardIndex)
        boards_T = boards.collect { b -> b.transpose() }
        wins.add(i)
    }
    if (boards.isEmpty()) {
        break
    }
}
println("score= ${getSumOfUnmarked(winningBoards.last()) * wins.last()}")
return 0

// ---------------------------------------------------------
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
        if (index <= 1) {
            order = it.split(',').collect { Integer.parseInt(it) }
        } else if (index == 2) {
            // skip
        } else if (line.isEmpty()) {
            boards.add(tempBoard)
            tempBoard = []
        } else {
            tempBoard.add(line.split(' ').findAll { !it.isEmpty() }.collect { [Integer.parseInt(it), false] })
        }
        lastLine = line
    }
    boards.add(tempBoard)
    return [order, boards]
}

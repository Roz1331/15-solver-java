import java.util.*;
import java.util.function.Function;

public class Solver {

    private static Board initial;
    private List<Board> result = new ArrayList<Board>();

    private class ITEM{
        private ITEM prevBoard;
        private Board board;

        private ITEM(ITEM prevBoard, Board board) {
            this.prevBoard = prevBoard;
            this.board = board;
        }
        public Board getBoard() {
            return board;
        }
    }

    public Solver(Board initial) {
        this.initial = initial;
        if(!Board.isValid()) {
            System.out.println("String is not valid");
            return;
        }

        PriorityQueue<ITEM> priorityQueue = new PriorityQueue<ITEM>(10, new Comparator<ITEM>() {
            @Override
            public int compare(ITEM o1, ITEM o2) {
                return Integer.valueOf(measure(o1)).compareTo(measure(o2));
            }
        });

        priorityQueue.add(new ITEM(null, initial));

        while (true){
            ITEM board = priorityQueue.poll(); //  шаг 2

            //  если дошли до решения, сохраняем весь путь ходов в лист
            if(board.board.isGoal()) {
                itemToList(new ITEM(board, board.board));
                return;
            }

            Iterator iterator = board.board.neighbors().iterator(); // соседи
            while (iterator.hasNext()){
                Board board1 = (Board) iterator.next();

                // оптимизация
                if(board1!= null && !containsInPath(board, board1))
                    priorityQueue.add(new ITEM(board, board1));
            }

        }
    }

    //  вычисляем f(x)
    private static int measure(ITEM item){
        ITEM item2 = item;
        int c= 0;   // g(x)
        int measure = item.getBoard().h();  // h(x)
        while (true){
            c++;
            item2 = item2.prevBoard;
            if(item2 == null) {
                // g(x) + h(x)
                return measure + c;
            }
        }
    }

    //  сохранение
    private void itemToList(ITEM item){
        ITEM item2 = item;
        while (true){
            item2 = item2.prevBoard;
            if(item2 == null) {
                Collections.reverse(result);
                return;
            }
            result.add(item2.board);
        }
    }

    // была ли уже такая позиция в пути
    private boolean containsInPath(ITEM item, Board board){
        ITEM item2 = item;
        while (true){
            if(item2.board.equals(board)) return true;
            item2 = item2.prevBoard;
            if(item2 == null) return false;
        }
    }

    public int moves() {
        if(!Board.isValid()) return -1;
        return result.size() - 1;
    }

    public Iterable<Board> solution() {
        return result;
    }

    public static int[][] initArrayFromString(String str) {
        int[][] res = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int num = str.charAt(i * 4 + j) - '0';
                res[i][j] = num < 10 ? num : num - 7;
            }
        }
        return res;
    }

    public static void TestSolution(String name, Function<Board, Integer> solution, int maxDepth) throws Exception {
        TestCase[] positions = { new TestCase("123456789ABCDEF0", 0), new TestCase("1234067859ACDEBF", 5),
                new TestCase("5134207896ACDEBF", 8), new TestCase("16245A3709C8DEBF", 10),
                new TestCase("1723068459ACDEBF", 13), new TestCase("12345678A0BE9FCD", 19),
                new TestCase("51247308A6BE9FCD", 27), new TestCase("F2345678A0BE91DC", 33),
                new TestCase("75123804A6BE9FCD", 35), new TestCase("75AB2C416D389F0E", 45),
                new TestCase("04582E1DF79BCA36", 48), new TestCase("FE169B4C0A73D852", 52),
                new TestCase("D79F2E8A45106C3B", 55), new TestCase("DBE87A2C91F65034", 58) };
        System.out.println("Start tests for "+name);
        for (TestCase testCase : positions) {
            if (testCase.expectedLength > maxDepth)
                break;
            var startedAt = System.nanoTime();
            int moves = solution.apply(new Board(testCase.position));
            var finishedAt = System.nanoTime();
            if (moves != testCase.expectedLength) throw new Exception(String.format("Expected %s, but was %s",
                    testCase.expectedLength, moves));
            System.out.println(String.format("Position %s, Number of moves = %d, expected = %d, time = %dms",
                    testCase.position, moves, testCase.expectedLength, (finishedAt - startedAt) / 1000000));
        }
    }

    public static void main(String[] args) throws Exception {
        // TestSolution("BFS", (board -> TODO), TODO);
        // TestSolution("IDDFS", (board -> TODO), TODO);
        TestSolution("A*", (board -> new Solver(board).moves()), 55);
        // TestSolution("IDA*", (board -> TODO), TODO);
    }
}
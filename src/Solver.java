import java.util.*;

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

    public static void main(String[] args) {
        String str = "51247308A6BE9FCD";
        String failStr = "F2345678A0BE91CD";

        Board initial = new Board(str);
        Solver solver = new Solver(initial);


        System.out.println("Number of moves = " + solver.moves());
    }
}
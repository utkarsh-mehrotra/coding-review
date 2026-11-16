import java.util.*;

public class ImplicitGraphs {

    static final int[][] DIRS4 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    static final int[][] DIRS8 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    // ---------- Flood fill / Number of Islands (LC 200) ----------
    // Iterative DFS: no recursion-depth risk on big grids. Marks visited in place.
    static int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length, count = 0;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] != '1') continue;
                count++;
                Deque<int[]> stack = new ArrayDeque<>();
                stack.push(new int[]{r, c});
                grid[r][c] = '0';
                while (!stack.isEmpty()) {
                    int[] cur = stack.pop();
                    for (int[] d : DIRS4) {
                        int nr = cur[0] + d[0], nc = cur[1] + d[1];
                        if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == '1') {
                            grid[nr][nc] = '0';
                            stack.push(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return count;
    }

    // ---------- Max Area of Island (LC 695): same traversal, count cells ----------
    static int maxAreaOfIsland(int[][] grid) {
        int m = grid.length, n = grid[0].length, best = 0;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] != 1) continue;
                int area = 0;
                Deque<int[]> stack = new ArrayDeque<>();
                stack.push(new int[]{r, c});
                grid[r][c] = 0;
                while (!stack.isEmpty()) {
                    int[] cur = stack.pop();
                    area++;
                    for (int[] d : DIRS4) {
                        int nr = cur[0] + d[0], nc = cur[1] + d[1];
                        if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 1) {
                            grid[nr][nc] = 0;
                            stack.push(new int[]{nr, nc});
                        }
                    }
                }
                best = Math.max(best, area);
            }
        }
        return best;
    }

    // ---------- Grid shortest path, 8 directions (LC 1091) ----------
    static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;
        boolean[][] seen = new boolean[n][n];
        Queue<int[]> q = new ArrayDeque<>(); // {r, c, pathLength}
        q.add(new int[]{0, 0, 1});
        seen[0][0] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            if (cur[0] == n - 1 && cur[1] == n - 1) return cur[2];
            for (int[] d : DIRS8) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0 && !seen[nr][nc]) {
                    seen[nr][nc] = true;
                    q.add(new int[]{nr, nc, cur[2] + 1});
                }
            }
        }
        return -1;
    }

    // ---------- Multi-source BFS on grid: Rotting Oranges (LC 994) ----------
    static int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length, fresh = 0;
        Queue<int[]> q = new ArrayDeque<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 2) q.add(new int[]{r, c});
                else if (grid[r][c] == 1) fresh++;
            }
        }
        int minutes = 0;
        while (!q.isEmpty() && fresh > 0) {
            minutes++;
            for (int sz = q.size(); sz > 0; sz--) { // process one BFS level per minute
                int[] cur = q.poll();
                for (int[] d : DIRS4) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        fresh--;
                        q.add(new int[]{nr, nc});
                    }
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

    // ---------- Reverse search from the sinks: Pacific Atlantic Water Flow (LC 417) ----------
    // Instead of searching from every cell, BFS uphill from each ocean's border.
    static List<List<Integer>> pacificAtlantic(int[][] h) {
        int m = h.length, n = h[0].length;
        boolean[][] pac = new boolean[m][n], atl = new boolean[m][n];
        Queue<int[]> qp = new ArrayDeque<>(), qa = new ArrayDeque<>();
        for (int r = 0; r < m; r++) {
            pac[r][0] = true; qp.add(new int[]{r, 0});
            atl[r][n - 1] = true; qa.add(new int[]{r, n - 1});
        }
        for (int c = 0; c < n; c++) {
            pac[0][c] = true; qp.add(new int[]{0, c});
            atl[m - 1][c] = true; qa.add(new int[]{m - 1, c});
        }
        bfsUphill(h, qp, pac);
        bfsUphill(h, qa, atl);
        List<List<Integer>> res = new ArrayList<>();
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (pac[r][c] && atl[r][c]) res.add(Arrays.asList(r, c));
        return res;
    }

    private static void bfsUphill(int[][] h, Queue<int[]> q, boolean[][] vis) {
        int m = h.length, n = h[0].length;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int[] d : DIRS4) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && !vis[nr][nc] && h[nr][nc] >= h[cur[0]][cur[1]]) {
                    vis[nr][nc] = true;
                    q.add(new int[]{nr, nc});
                }
            }
        }
    }

    // ---------- Word Ladder (LC 127): neighbors generated on the fly ----------
    static int wordLadder(String begin, String end, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(end)) return 0;
        Queue<String> q = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        q.add(begin);
        seen.add(begin);
        int steps = 1;
        while (!q.isEmpty()) {
            for (int sz = q.size(); sz > 0; sz--) {
                String w = q.poll();
                if (w.equals(end)) return steps;
                char[] cs = w.toCharArray();
                for (int i = 0; i < cs.length; i++) {
                    char orig = cs[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        if (ch == orig) continue;
                        cs[i] = ch;
                        String nxt = new String(cs);
                        if (dict.contains(nxt) && seen.add(nxt)) q.add(nxt);
                    }
                    cs[i] = orig;
                }
            }
            steps++;
        }
        return 0;
    }

    // ---------- Bidirectional BFS: expand the smaller frontier, stop when frontiers touch ----------
    // Cuts the search from ~b^d to ~2*b^(d/2). Use when both endpoints are known and branching is high.
    static int wordLadderBidirectional(String begin, String end, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(end)) return 0;
        if (begin.equals(end)) return 1;
        Set<String> front = new HashSet<>(), back = new HashSet<>(), seen = new HashSet<>();
        front.add(begin);
        back.add(end);
        seen.add(begin);
        seen.add(end);
        int steps = 1;
        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) { Set<String> t = front; front = back; back = t; }
            Set<String> next = new HashSet<>();
            for (String w : front) {
                char[] cs = w.toCharArray();
                for (int i = 0; i < cs.length; i++) {
                    char orig = cs[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        if (ch == orig) continue;
                        cs[i] = ch;
                        String nxt = new String(cs);
                        if (back.contains(nxt)) return steps + 1; // frontiers met
                        if (dict.contains(nxt) && seen.add(nxt)) next.add(nxt);
                    }
                    cs[i] = orig;
                }
            }
            front = next;
            steps++;
        }
        return 0;
    }

    // ---------- State-space BFS: Open the Lock (LC 752). Node = 4-digit string. ----------
    static int openLock(String[] deadends, String target) {
        Set<String> dead = new HashSet<>(Arrays.asList(deadends));
        if (dead.contains("0000")) return -1;
        Set<String> seen = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        q.add("0000");
        seen.add("0000");
        int steps = 0;
        while (!q.isEmpty()) {
            for (int sz = q.size(); sz > 0; sz--) {
                String s = q.poll();
                if (s.equals(target)) return steps;
                for (int i = 0; i < 4; i++) {
                    for (int delta : new int[]{1, 9}) { // +1 and -1 (mod 10)
                        char[] cs = s.toCharArray();
                        cs[i] = (char) ('0' + (cs[i] - '0' + delta) % 10);
                        String nxt = new String(cs);
                        if (!dead.contains(nxt) && seen.add(nxt)) q.add(nxt);
                    }
                }
            }
            steps++;
        }
        return -1;
    }

    // ---------- State-space BFS: Sliding Puzzle (LC 773). Node = board serialized as a string. ----------
    static int slidingPuzzle(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) for (int v : row) sb.append(v);
        String start = sb.toString(), goal = "123450";
        int[][] nbrs = {{1, 3}, {0, 2, 4}, {1, 5}, {0, 4}, {1, 3, 5}, {2, 4}}; // indices adjacent to each cell
        Set<String> seen = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        q.add(start);
        seen.add(start);
        int steps = 0;
        while (!q.isEmpty()) {
            for (int sz = q.size(); sz > 0; sz--) {
                String s = q.poll();
                if (s.equals(goal)) return steps;
                int zero = s.indexOf('0');
                for (int nb : nbrs[zero]) {
                    char[] cs = s.toCharArray();
                    cs[zero] = cs[nb];
                    cs[nb] = '0';
                    String nxt = new String(cs);
                    if (seen.add(nxt)) q.add(nxt);
                }
            }
            steps++;
        }
        return -1;
    }
}

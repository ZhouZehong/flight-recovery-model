package assign;

import java.util.Arrays;

/**
 * KM实现匈牙利算法最优飞机-航班串匹配问题
 */
public class KM {
    /** 飞机指派给航班串的延时指派矩阵 */
    private long[][] delays;
    /** 飞机的标杆值 & 航班串的标杆值 */
    private long[] planeValue, flightValue;
    /** 记录每一轮匹配过的飞机和航班串 */
    private boolean[] visitPlane, visitFlight;
    /** 记录每个航班串如果能被飞机匹配上最少还需要多少标杆值 */
    private long[] slack;
    /** 记录每个航班串匹配到的飞机，如果没有则为-1 */
    private int[] match;

    /** 航班串/飞机数量的上限 */
    private int maxN;
    private int n;
    /** 飞机的数量 */
    private int lenPlane;
    /** 航班串的数量 */
    private int lenFlight;

    public KM(int mxaN){
        this.maxN = mxaN;
        planeValue = new long[maxN];
        flightValue = new long[maxN];
        visitPlane = new boolean[maxN];
        visitFlight = new boolean[maxN];
        slack = new long[maxN];
        match = new int[maxN];
    }

    /**
     * 预处理工作
     * @param delay 延时指派矩阵
     * @return 是否满足开始工作的需求
     */
    private boolean preProcess(long[][] delay){
        if (delay == null){
            return false;
        }
        lenPlane = delay.length; // 二维数组有几行
        lenFlight = delay[0].length; // 二维数组有几列
        if (lenPlane > maxN || lenFlight > maxN){
            return false;
        }
        // 初始化匹配记录
        Arrays.fill(match, -1);
        n = Math.max(lenPlane, lenFlight);
        delays = new long[n][n];
        for (int i = 0; i < n ; i++) {
            Arrays.fill(delays[i], 0);
        }
        for (int i = 0; i < lenPlane ; i++) {
            for (int j = 0; j < lenFlight; j++) {
                delays[i][j] = delay[i][j];
            }
        }
        return true;
    }

    /**
     * 判断是否能找到深度优先搜索增广路径
     * @param node 搜索增广路径的起点
     * @return 能否找到路径
     */
    private boolean findPath(int node){
        visitPlane[node] = true;
        for (int i = 0; i < n; i++) {
            if (!visitFlight[i]){
                long temp = planeValue[node] + flightValue[i] - delays[node][i];
                if (temp == 0){
                    visitFlight[i] = true;
                    if (match[i] == -1 || findPath(match[i])){
                        match[i] = node;
                        return true;
                    }
                }
                else
                    slack[i] = Math.min(slack[i], temp);
            }
        }
        return false;
    }

    /**
     * 得到最优的指派方案
     * @param delay 指派延时矩阵
     * @return 最优的指派方案
     */
    public int[][] getMaxBipartie(long[][] delay){
        long result;
        if (!preProcess(delay))
        {
            result = 0;
            return null;
        }

        // 初始化飞机和航班串的标杆值
        Arrays.fill(planeValue, 0);
        Arrays.fill(flightValue, 0);
        for (int i = 0; i < n ; i++) {
            for (int j = 0; j < n; j++) {
                if (planeValue[i] < delays[i][j]){
                    planeValue[i] = delays[i][j];
                }
            }
        }

        // 开始给飞机匹配航班串
        for (int i = 0; i < n; i++) {
            Arrays.fill(slack, 0x7fffffff); // 默认是无穷大
            while (true){
                Arrays.fill(visitPlane, false);
                Arrays.fill(visitFlight, false);
                if (findPath(i)){
                    break;
                }
                long inc = 0x7fffffff;
                for (int j = 0; j < n; j++) {
                    if (!visitFlight[j] && slack[j] < inc){
                        inc = slack[j];
                    }
                }
                for (int j = 0; j < n; j++) {
                    if (visitPlane[j]){
                        planeValue[j] -= inc;
                    }
                    if (visitFlight[j]){
                        flightValue[j] += inc;
                    }
                }
            }
        }
        result = 0;
        for (int i = 0; i < n; i++) {
            if (match[i] >= 0){
                result += delays[match[i]][i];
            }
        }
        return matchResult();
    }

    public int[][] matchResult(){
        int len = Math.min(lenPlane, lenFlight);
        int[][] res = new int[len][2];
        int count = 0;
        for (int i = 0; i < lenFlight; i++) {
            if (match[i] >= 0 && match[i] < lenPlane){
                res[count][0] = match[i];
                res[count++][1] = i;
            }
        }
        return res;
    }
}

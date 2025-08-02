import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class runtest2 {

    static int count = 0;
    static int count2 = 0;
    static int count3 = 0;

    public static synchronized void increment() {
        count2++;
    }

    static volatile boolean running = true;

    static AtomicInteger count5 = new AtomicInteger(0);

    static int count7 = 0;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            for (int i = 0; i < 1000; i++) {
                count++;
            }
        };

        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task1);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 스레드들이 동시에 같은 변수의 값을 읽고 계산해서, 서로의 결과를 덮어쓰기 때문에 올바른 총합이 나오지 않는다.
        System.out.println("최종 count 값: " + count); // 스레드 경쟁 상태 (race condition)

        // 위와 같이 값이 계속 달라지는거 해결 방법은?

        Runnable task2 = () -> {
            for (int i = 0; i < 1000; i++) {
                increment();
            }
        };

        Thread t3 = new Thread(task2);
        Thread t4 = new Thread(task2);

        t3.start();
        t4.start();

        t3.join();
        t4.join();

        //🔒 synchronized = 계산기방에 ‘한 사람씩’만 들어가게 함
        // - A가 방에 들어감 (다른 애들 대기)
        // - A가 계산하고 저장 후 나옴
        // - B가 그 다음에 들어가서 계산함
        //👍 그래서 덮어쓰기 없이 모든 작업이 순서대로 처리됨


        System.out.println("최종 count 값: " + count2);

        // 블록 방식
        Runnable task3 = () -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (runtest2.class) {
                    count3++;
                }
            }
        };

        Thread t5 = new Thread(task3);
        Thread t6 = new Thread(task3);

        t5.start();
        t6.start();

        t5.join();
        t6.join();

        System.out.println("최종 count 값: " + count3);


        // volatile = 변수의 최신 값 유지 (읽기/쓰기 일관성)
        // 단순한 “읽기/쓰기 일관성 보장”만 된다.
        Runnable task4 = () -> {
            while (running) {
                // 작업 수행 중...
            }
            System.out.println("스레드 종료됨");
        };

        Thread t7 = new Thread(task4);
        t7.start();

        Thread.sleep(1000); // 1초 후 종료
        running = false;

        t7.join();
        System.out.println("종료");

        Runnable task5 = () -> {
            for (int i = 0; i < 1000; i++){
                count5.incrementAndGet(); // 안전한 ++
            }
        };

        Thread t8 = new Thread(task5);
        Thread t9 = new Thread(task5);

        t8.start();
        t9.start();

        t8.join();
        t9.join();

        System.out.println("최종 count 값: " + count5.get());

        Runnable task6 = () -> {
            for (int i = 0; i < 1000; i++) {
                lock.lock();
                try {
                    count7++;
                } finally {
                    lock.unlock(); // 반드시 해제해야 함!
                }
            }
        };

        Thread t10 = new Thread(task6);
        Thread t11 = new Thread(task6);

        t10.start();
        t11.start();

        t10.join();
        t11.join();

        System.out.println("최종 count 값: " + count);

        Runnable task7 = () -> {
            System.out.println(Thread.currentThread().getName() + " 작업 시작");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("인터럽트 발생");
            }
            System.out.println(Thread.currentThread().getName() + " 작업 끝");
        };

        executor.submit(task7);
        executor.submit(task7);
        executor.submit(task7);  // 스레드 2개지만 작업은 3개 → 큐에 대기

        executor.shutdown(); // 더 이상 작업 안 받겠다는 뜻
    }
}



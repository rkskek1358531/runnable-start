public class runtest1 {

    public static void main(String[] args) throws InterruptedException {

        Runnable task = () -> System.out.println("람다 실행중...");

        new Thread(task).start();

        Runnable task2 = () -> {
            for (int i = 10; i > 0; i--) {
                System.out.println("남은 시간 타이머1:" + i + "초");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("인터럽트 발생!");
                }
            }
        };

        Thread t2 = new Thread(task2);
        t2.start();
        t2.join();

        Runnable task3 = () -> {
            for (int i = 5; i > 0; i--) {
                System.out.println("남은 시간 타이머2:" + i + "초");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("인터럽트 발생!");
                }
            }
        };

        Thread t3 = new Thread(task3);
        t3.start();
        t3.join();
    }
}

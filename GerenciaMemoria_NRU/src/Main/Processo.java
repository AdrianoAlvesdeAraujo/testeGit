package Main;

public class Processo extends Thread {

    private int id;

    private MMU mmu;
    private String[] comandos;
    private Status status;

    public Processo(int id_, MMU mmu_, String[] comando, Status s) {
        this.id = id_;
        this.mmu = mmu_;
        this.comandos = comando;
        this.status = s;
    }

    public void process() {

        for (String g : comandos) {

            System.out.println("Processo: " + this.id + " ACORDOU!@##@!!");
            mmu.receberComando(g, this.id);
            System.out.println(g);

            try {
            status.status_fim();
                System.out.println("Processo: " + this.id + " Dormiu\n\n");
                Thread.sleep(this.id * 1501); //

            } catch (InterruptedException e) {

                System.out.println(e.getMessage());
            }

        }
        System.out.println("Processo: " + this.id + " Acabou\n");

        status.status_fim();

    }

    @Override
    public void run() {

        process();

    }
}

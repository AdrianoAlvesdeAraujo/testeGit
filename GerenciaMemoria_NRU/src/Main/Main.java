package Main;

import Memoria.MemHD;
import Memoria.MemRam;
import Memoria.MemVirtual;

public class Main {

    final static int QUANTIDADE_THREADS = 3;

    public static void main(String[] args) {

        final int TAMANHO_VIRTUAL = 16;

        MemRam MenFisica = new MemRam(TAMANHO_VIRTUAL / 2);
        MemVirtual MV = new MemVirtual(TAMANHO_VIRTUAL);
        MemHD HD = new MemHD();
        Status status = new Status(MenFisica, MV, HD);
        MMU mmu = new MMU(MV, MenFisica, HD);

        try {
            for (int i = 1; i <= QUANTIDADE_THREADS; i++) {

                String SUA_ENTRADA = new FabricaDeEntradas(TAMANHO_VIRTUAL).getNewEntrada();
                String comandos[] = SUA_ENTRADA.split(",");
                new Processo(i, mmu, comandos, status).start();
            }

        } catch (Exception e2) {
            System.out.println(e2.getMessage());
        }

    }

}

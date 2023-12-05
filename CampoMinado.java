package campominado12;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class CampoMinado {
    abstract class Celula extends JButton {
        int linha;
        int coluna;
        boolean aberta;
        boolean temMina;

        public Celula(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
            this.aberta = false;
            this.temMina = false;
        }

        abstract void revelar();
    }

    class CelulaVazia extends Celula {
        public CelulaVazia(int linha, int coluna) {
            super(linha, coluna);
        }

        @Override
        void revelar() {
            if (!this.aberta) {
                abrirCelula(this);
            }
        }
    }

    class CelulaBomba extends Celula {
        public CelulaBomba(int linha, int coluna) {
            super(linha, coluna);
            this.temMina = true;

            this.setFocusable(false);
            this.setMargin(new Insets(0, 0, 0, 0));
            this.setFont(new Font("Minecraft Evenings", Font.PLAIN, 25));
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            this.setBackground(Color.LIGHT_GRAY); // Definindo a cor de fundo

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (FimDeJogo || aberta) {
                        return;
                    }
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        revelar();
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        marcarBandeira(CelulaBomba.this);
                    }
                }
            });
        }

        @Override
        void revelar() {
            mostrarBombas();
        }
    }

    int TamanhoDosQuadradinhos = 40;
    int NumeroDeLinhasTotal = 32;
    int NumeroDeColunasTotal = NumeroDeLinhasTotal;
    int LarguraTabuleiro = NumeroDeColunasTotal * TamanhoDosQuadradinhos;
    int AlturaTabuleiro = NumeroDeLinhasTotal * TamanhoDosQuadradinhos;

    JFrame JanelaInicial = new JFrame("Campo Minado");
    JLabel TextoDeTopo = new JLabel();
    JPanel PainelDoTexto = new JPanel();
    JPanel PainelDosQuadradinhos = new JPanel();

    int QuantidadeDeBombasNaPartida = 100;
    Celula[][] MatrizDoTabuleiro = new Celula[NumeroDeLinhasTotal][NumeroDeColunasTotal];
    Random random = new Random();

    int NumeroDeQuadradosClicados = 0;
    boolean FimDeJogo = false;

    CampoMinado() {
        JanelaInicial.setSize(LarguraTabuleiro, AlturaTabuleiro);
        JanelaInicial.setLocationRelativeTo(null);
        JanelaInicial.setResizable(false);
        JanelaInicial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JanelaInicial.setLayout(new BorderLayout());

        TextoDeTopo.setFont(new Font("Arial", Font.BOLD, 25));
        TextoDeTopo.setHorizontalAlignment(JLabel.CENTER);
        TextoDeTopo.setText("Campo Minado: " + Integer.toString(QuantidadeDeBombasNaPartida));
        TextoDeTopo.setOpaque(true);

        PainelDoTexto.setLayout(new BorderLayout());
        PainelDoTexto.add(TextoDeTopo);
        JanelaInicial.add(PainelDoTexto, BorderLayout.NORTH);

        PainelDosQuadradinhos.setLayout(new GridLayout(NumeroDeLinhasTotal, NumeroDeColunasTotal));
        JanelaInicial.add(PainelDosQuadradinhos);

        for (int Linha = 0; Linha < NumeroDeLinhasTotal; Linha++) {
            for (int Coluna = 0; Coluna < NumeroDeColunasTotal; Coluna++) {
                Celula celula = new CelulaVazia(Linha, Coluna);
                MatrizDoTabuleiro[Linha][Coluna] = celula;

                celula.setFocusable(false);
                celula.setMargin(new Insets(0, 0, 0, 0));
                celula.setFont(new Font("Minecraft Evenings", Font.PLAIN, 25));
                celula.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celula.setBackground(Color.LIGHT_GRAY);

                celula.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (FimDeJogo || celula.aberta) {
                            return;
                        }
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            celula.revelar();
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            marcarBandeira(celula);
                        }
                    }
                });

                PainelDosQuadradinhos.add(celula);
            }
        }

        JanelaInicial.setVisible(true);
        distribuidorDeBombas();
    }

    void distribuidorDeBombas() {
        int bombasRestantes = QuantidadeDeBombasNaPartida;
        while (bombasRestantes > 0) {
            int linha = random.nextInt(NumeroDeLinhasTotal);
            int coluna = random.nextInt(NumeroDeColunasTotal);

            Celula celula = MatrizDoTabuleiro[linha][coluna];
            if (!(celula instanceof CelulaBomba)) {
                PainelDosQuadradinhos.remove(celula);
                Celula novaCelula = new CelulaBomba(linha, coluna);
                MatrizDoTabuleiro[linha][coluna] = novaCelula;
                PainelDosQuadradinhos.add(novaCelula, linha * NumeroDeColunasTotal + coluna);
                bombasRestantes--;
            }
        }
        PainelDosQuadradinhos.revalidate();
        PainelDosQuadradinhos.repaint();
    }

    void mostrarBombas() {
        for (int linha = 0; linha < NumeroDeLinhasTotal; linha++) {
            for (int coluna = 0; coluna < NumeroDeColunasTotal; coluna++) {
                Celula celula = MatrizDoTabuleiro[linha][coluna];
                if (celula instanceof CelulaBomba) {
                    celula.setText("💣");
                }
            }
        }

        FimDeJogo = true;
        TextoDeTopo.setText("Game Over!");
    }

    void abrirCelula(Celula celula) {
        if (celula.aberta) {
            return;
        }

        if (celula.temMina) {
            mostrarBombas();
            return;
        }

        celula.aberta = true;
        celula.setBackground(Color.WHITE);

        int minasEncontradas = contadorDeMinas(celula.linha, celula.coluna);

        if (minasEncontradas > 0) {
            celula.setText(Integer.toString(minasEncontradas));
        } else {
            abridorEmCadeia(celula.linha, celula.coluna);
        }

        NumeroDeQuadradosClicados++;

        if (NumeroDeQuadradosClicados == NumeroDeLinhasTotal * NumeroDeColunasTotal - QuantidadeDeBombasNaPartida) {
            FimDeJogo = true;
            TextoDeTopo.setText("Mines Cleared!");
        }
    }

    int contadorDeMinas(int linha, int coluna) {
        int minasEncontradas = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = linha + dr;
                int nc = coluna + dc;
                if (nr >= 0 && nr < NumeroDeLinhasTotal && nc >= 0 && nc < NumeroDeColunasTotal) {
                    Celula vizinha = MatrizDoTabuleiro[nr][nc];
                    if (vizinha instanceof CelulaBomba) {
                        minasEncontradas++;
                    }
                }
            }
        }
        return minasEncontradas;
    }

    void abridorEmCadeia(int linha, int coluna) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = linha + dr;
                int nc = coluna + dc;
                if (nr >= 0 && nr < NumeroDeLinhasTotal && nc >= 0 && nc < NumeroDeColunasTotal) {
                    Celula vizinha = MatrizDoTabuleiro[nr][nc];
                    if (!vizinha.aberta) {
                        abrirCelula(vizinha);
                    }
                }
            }
        }
    }


    void marcarBandeira(Celula celula) {
        if (!celula.aberta) {
            if (celula.getText().isEmpty()) {
                celula.setText("🚩");
            } else {
                celula.setText("");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CampoMinado();
            }
        });
    }
}

package campominado12;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class CampoMinado {
    private class CriadorDeQuadrado extends JButton {
        int Linha;
        int Coluna;
        boolean CTemMina;
        boolean CAberta;

        public CriadorDeQuadrado(int Linha, int Coluna) {
            this.Linha = Linha;
            this.Coluna = Coluna;
            this.CTemMina = false;
            this.CAberta = false;
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
    CriadorDeQuadrado[][] MatrizDoTabuleiro = new CriadorDeQuadrado[NumeroDeLinhasTotal][NumeroDeColunasTotal];
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
                CriadorDeQuadrado Quadrado = new CriadorDeQuadrado(Linha, Coluna);
                MatrizDoTabuleiro[Linha][Coluna] = Quadrado;

                Quadrado.setFocusable(false);
                Quadrado.setMargin(new Insets(0, 0, 0, 0));
                Quadrado.setFont(new Font("Minecraft Evenings", Font.PLAIN, 25));
                Quadrado.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                Quadrado.setBackground(Color.LIGHT_GRAY); 

                Quadrado.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (FimDeJogo || Quadrado.CAberta) {
                            return;
                        }
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (Quadrado.CTemMina) {
                                mostrarBombas();
                            } else {
                                abrirQuadrado(Quadrado);
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (!Quadrado.CAberta) {
                                enfiaBandeira(Quadrado);
                            }
                        }
                    }
                });

                PainelDosQuadradinhos.add(Quadrado);
            }
        }

        JanelaInicial.setVisible(true);
        distribuidorDeBombas();
    }

    void distribuidorDeBombas() {
        int mineLeft = QuantidadeDeBombasNaPartida;
        while (mineLeft > 0) {
            int Linha = random.nextInt(NumeroDeLinhasTotal);
            int Coluna = random.nextInt(NumeroDeColunasTotal);

            if (!MatrizDoTabuleiro[Linha][Coluna].CTemMina) {
                MatrizDoTabuleiro[Linha][Coluna].CTemMina = true;
                mineLeft -= 1;
            }
        }
    }

    void mostrarBombas() {
        for (int Linha = 0; Linha < NumeroDeLinhasTotal; Linha++) {
            for (int Coluna = 0; Coluna < NumeroDeColunasTotal; Coluna++) {
                CriadorDeQuadrado Quadrado = MatrizDoTabuleiro[Linha][Coluna];
                if (Quadrado.CTemMina) {
                    Quadrado.setText("O");
                }
            }
        }

        FimDeJogo = true;
        TextoDeTopo.setText("Game Over!");
    }

    void abrirQuadrado(CriadorDeQuadrado Quadrado) {
        if (Quadrado.CAberta || Quadrado.CTemMina) {
            return;
        }

        Quadrado.CAberta = true;
        Quadrado.setBackground(Color.WHITE);

        int minasEncontradas = contadorDeMinas(Quadrado.Linha, Quadrado.Coluna);

        if (minasEncontradas > 0) {
            Quadrado.setText(Integer.toString(minasEncontradas));
        } else {
            abridorEmCadeia(Quadrado.Linha, Quadrado.Coluna);
        }

        NumeroDeQuadradosClicados++;

        if (NumeroDeQuadradosClicados == NumeroDeLinhasTotal * NumeroDeColunasTotal - QuantidadeDeBombasNaPartida) {
            FimDeJogo = true;
            TextoDeTopo.setText("Mines Cleared!");
        }
    }

    int contadorDeMinas(int Linha, int Coluna) {
        int minasEncontradas = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = Linha + dr;
                int nc = Coluna + dc;
                if (nr >= 0 && nr < NumeroDeLinhasTotal && nc >= 0 && nc < NumeroDeColunasTotal) {
                    if (MatrizDoTabuleiro[nr][nc].CTemMina) {
                        minasEncontradas++;
                    }
                }
            }
        }
        return minasEncontradas;
    }

    void abridorEmCadeia(int Linha, int Coluna) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = Linha + dr;
                int nc = Coluna + dc;
                if (nr >= 0 && nr < NumeroDeLinhasTotal && nc >= 0 && nc < NumeroDeColunasTotal) {
                    if (!MatrizDoTabuleiro[nr][nc].CAberta) {
                        abrirQuadrado(MatrizDoTabuleiro[nr][nc]);
                    }
                }
            }
        }
    }

    void enfiaBandeira(CriadorDeQuadrado Quadrado) {
        if (!Quadrado.CAberta) {
            if (Quadrado.getText().isEmpty()) {
                Quadrado.setText("🚩");
            } else {
                Quadrado.setText("");
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
package com.example.root.jiu;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    //1 - Declara uma referencia para a superficie de desenho (o que vai ser apresentado na tela quando rodar a aplicação)
    GLSurfaceView superficieDesenho=null;
    //4-Declara uma referencia para o render
    Renderizador render=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //2 - Instanciando o objeto da superficie do desenho
        superficieDesenho = new GLSurfaceView(this);

        //Instancia um objeto renderizador
        render=new Renderizador();

        //Configura o objeto de desenho da superficie
        superficieDesenho.setRenderer(render);//Fazendo a ligação entre os metodos que controlam o desenho e a superficie de desenho

        //3 - publicar a superficie de denho na tela
        setContentView(superficieDesenho);
    }
}


//GL10 é a classe OpenGL importada
//Classe que ira implementar a logica do desenho
class Renderizador implements Renderer{

    private long tempoInicial=0;
    private long tempoAtual=0;
    private int contaQuadros=0;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {//Chamado quando a superficie de desenho é criada
        tempoInicial = System.currentTimeMillis();
        tempoAtual = System.currentTimeMillis();

        //Seta a cor da limpeza de tela
        gl10.glClearColor(0, 0, 1, 1);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int largura, int altura) {//Chamado quando a superficie de desenho sofre alteração
        Log.i("INFO", largura + "" + altura);

        //Configurando a area de coordenadas do plano cartesiano
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        //Configurando o volume de renderização (dimensões)
        gl.glOrthof(0.0f,largura, 0.0f,altura,-1.0f,1.0f);//POSSIVEL ERRO

        //Configurando a matriz de transferencia geometricas (translação, rotação e escala)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        //Configura a area de visualização na tela do dispositivo
        gl.glViewport(0,0,largura,altura);

        //habilita o desenho por vertices
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //Aloca memoria e define as coordenadas da primitiva
        float [] vetCoords1={
                largura/2,altura/2,
                0.0f,altura/2,
                largura/2,0.0f,
                0.0f,0.0f,
                };//Coordenadas dos vertices

        FloatBuffer buffer1=generateBuffer(vetCoords1);//Transformando o vetor Java em um floatBuffer

        gl.glVertexPointer(2,GL10.GL_FLOAT,0,buffer1);//Registrar na OpenGL
    }

    @Override
    public void onDrawFrame(GL10 gl10) {//Metodo chamado N vezes para desenhar na tela (Mede o FPS)

        float vermelho = (float) Math.random();
        float verde = (float) Math.random();
        float azul = (float) Math.random();

        tempoAtual=System.currentTimeMillis();
        contaQuadros++;

        if (tempoAtual - tempoInicial > 1000) {
            tempoInicial=System.currentTimeMillis();
            contaQuadros=0;
            gl10.glClearColor(vermelho, verde, azul, 1.0f);
        }


        //Configura a cor de limpeza no formato RGBA
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glColor4f(1.0f,0.0f,0.0f,1.0f);//Define a cor que vai ser utilizada para desenhar
        gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);//Count é o numero de vertices
    }

    FloatBuffer generateBuffer(float [] vetor){
        ByteBuffer prBuffer=ByteBuffer.allocateDirect(vetor.length*4);//Aloca memoria em bytes

        prBuffer.order(ByteOrder.nativeOrder());//Ordena os endereços de memoria conforme a arquitetura do processador

        FloatBuffer prFloat=prBuffer.asFloatBuffer();
        prFloat.clear();//Limpa lixo de memoria

        prFloat.put(vetor);//Gera o encapsulador

        prFloat.flip();//Faz o ajuste da memoria

        return prFloat;
    }
}

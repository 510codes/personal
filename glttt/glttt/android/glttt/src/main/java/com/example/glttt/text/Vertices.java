package com.example.glttt.text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Vertices {

    final static int POSITION_CNT_2D = 2;              // Number of Components in Vertex Position for 2D
    final static int TEXCOORD_CNT = 2;                 // Number of Components in Vertex Texture Coords
    private static final int MVP_MATRIX_INDEX_CNT = 1; // Number of Components in MVP matrix index

    final static int INDEX_SIZE = Short.SIZE / 8;      // Index Byte Size (Short.SIZE = bits)

    private static final String TAG = "Vertices";

    //--Members--//
    // NOTE: all members are constant, and initialized in constructor!
    public final int positionCnt;                      // Number of Position Components (2=2D, 3=3D)
    public final int vertexStride;                     // Vertex Stride (Element Size of a Single Vertex)
    public final int vertexSize;                       // Bytesize of a Single Vertex
    final IntBuffer vertices;                          // Vertex Buffer
    final ShortBuffer indices;                         // Index Buffer
    public int numVertices;                            // Number of Vertices in Buffer
    public int numIndices;                             // Number of Indices in Buffer
    final int[] tmpBuffer;                             // Temp Buffer for Vertex Conversion

    //--Constructor--//
    // D: create the vertices/indices as specified (for 2d/3d)
    // A: maxVertices - maximum vertices allowed in buffer
    //    maxIndices - maximum indices allowed in buffer
    public Vertices(int maxVertices, int maxIndices)  {
        //      this.gl = gl;                                   // Save GL Instance
        this.positionCnt = POSITION_CNT_2D;  // Set Position Component Count
        this.vertexStride = this.positionCnt + TEXCOORD_CNT + MVP_MATRIX_INDEX_CNT;  // Calculate Vertex Stride
        this.vertexSize = this.vertexStride * 4;        // Calculate Vertex Byte Size

        ByteBuffer buffer = ByteBuffer.allocateDirect( maxVertices * vertexSize );  // Allocate Buffer for Vertices (Max)
        buffer.order( ByteOrder.nativeOrder() );        // Set Native Byte Order
        this.vertices = buffer.asIntBuffer();           // Save Vertex Buffer

        if ( maxIndices > 0 )  {                        // IF Indices Required
            buffer = ByteBuffer.allocateDirect( maxIndices * INDEX_SIZE );  // Allocate Buffer for Indices (MAX)
            buffer.order( ByteOrder.nativeOrder() );     // Set Native Byte Order
            this.indices = buffer.asShortBuffer();       // Save Index Buffer
        }
        else                                            // ELSE Indices Not Required
            indices = null;                              // No Index Buffer

        numVertices = 0;                                // Zero Vertices in Buffer
        numIndices = 0;                                 // Zero Indices in Buffer

        this.tmpBuffer = new int[maxVertices * vertexSize / 4];  // Create Temp Buffer
    }

    //--Set Vertices--//
    // D: set the specified vertices in the vertex buffer
    //    NOTE: optimized to use integer buffer!
    // A: vertices - array of vertices (floats) to set
    //    offset - offset to first vertex in array
    //    length - number of floats in the vertex array (total)
    //             for easy setting use: vtx_cnt * (this.vertexSize / 4)
    // R: [none]
    public void setVertices(float[] vertices, int offset, int length)  {
        this.vertices.clear();                          // Remove Existing Vertices
        int last = offset + length;                     // Calculate Last Element
        for ( int i = offset, j = 0; i < last; i++, j++ )  // FOR Each Specified Vertex
            tmpBuffer[j] = Float.floatToRawIntBits( vertices[i] );  // Set Vertex as Raw Integer Bits in Buffer
        this.vertices.put( tmpBuffer, 0, length );      // Set New Vertices
        this.vertices.flip();                           // Flip Vertex Buffer
        this.numVertices = length / this.vertexStride;  // Save Number of Vertices
    }

    //--Set Indices--//
    // D: set the specified indices in the index buffer
    // A: indices - array of indices (shorts) to set
    //    offset - offset to first index in array
    //    length - number of indices in array (from offset)
    // R: [none]
    public void setIndices(short[] indices, int offset, int length)  {
        this.indices.clear();                           // Clear Existing Indices
        this.indices.put( indices, offset, length );    // Set New Indices
        this.indices.flip();                            // Flip Index Buffer
        this.numIndices = length;                       // Save Number of Indices
    }
}
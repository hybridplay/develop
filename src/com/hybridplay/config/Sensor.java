package com.hybridplay.config;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class Sensor {
	
	private float actualValue;
	private float maxValue, minValue, centerValue;
	private static float minDistValue = 40;  //minimo para no tocar el centro
    private static float speedMinDistValue = 0.5f; //velocidad con la que se acercan al centro
    private float   fireMax = 0, fireMin = 1024; //limites de disparo de accion
    private String sensorName;
    private boolean fireMaxActive = false;
    private boolean fireMinActive = false;
    
    public Sensor(String sensorName){
    	actualValue = 0;
    	maxValue = 0; 
    	minValue = 1024; 
    	centerValue =0;
    	this.sensorName = sensorName;
    }
    
    public void update(float x){
    	actualValue = x;
    	
        //actulizamos los max y minimos
        if (actualValue > maxValue){
        	maxValue = actualValue;
        }else if(actualValue < minValue){
        	minValue = actualValue;
        }
        
        float distancia = maxValue - minValue;
        speedMinDistValue = distancia / 10f;
        		
        //acercamos los max y min al centro en cada iteraccion
        if (actualValue + minDistValue < maxValue) maxValue = maxValue - speedMinDistValue;
        if (actualValue > minValue + minDistValue) minValue = minValue + speedMinDistValue;
        //if (actualValue + minDistValue < maxValue) maxValue = maxValue - (maxValue - minValue / 10f);
        //if (actualValue > minValue + minDistValue) minValue = minValue + (maxValue - minValue / 10f);
        
        //calculamos el centro
        centerValue = minValue + ((maxValue - minValue) / 2);
        
        //calculamos los limites de los disparadores
        fireMax = centerValue + ((maxValue - centerValue) / 2);
        fireMin = minValue + ((centerValue - minValue) / 2);
        
        //comprobamos si estamos disaparando el max y lo dibujamos
        if (actualValue > fireMax) {
        	fireMaxActive = true;
        }else{
        	fireMaxActive = false;
        }
        
      //comprobamos si estamos disparando el min y lo dibujamos
        if (actualValue < fireMin) {
        	fireMinActive = true;
        }else{
        	fireMinActive = false;
        }
        
        
    }
    
    public Canvas draw(Canvas mCanvas, Paint paint, int mColor, int xDrawing, int yDrawing) {
    	
    	  paint.setColor(mColor);
          paint.setStyle(Paint.Style.STROKE); 
          paint.setStrokeWidth(2.5f);
          
          mCanvas.drawRect(xDrawing, yDrawing + 0, xDrawing + 30, yDrawing + actualValue, paint);

          if(fireMaxActive){
          	paint.setColor(Color.RED);
          	paint.setStyle(Paint.Style.FILL);
          	mCanvas.drawRect(xDrawing, yDrawing + fireMax, xDrawing + 30, yDrawing + maxValue, paint);
          	paint.setStyle(Paint.Style.STROKE);         	
          }
          
          if(fireMinActive){
          	paint.setColor(Color.RED);
          	paint.setStyle(Paint.Style.FILL);
          	mCanvas.drawRect(xDrawing , yDrawing + minValue, xDrawing + 30, yDrawing + fireMin, paint);
          	paint.setStyle(Paint.Style.STROKE);         	
          }
         
          //dibujamos todas las lineas
          paint.setColor(Color.RED);
          mCanvas.drawLine(xDrawing, yDrawing +  maxValue, xDrawing + 30, yDrawing +  maxValue, paint); //maximo
          paint.setColor(Color.GRAY);
          mCanvas.drawLine(xDrawing, yDrawing + fireMax, xDrawing + 30, yDrawing + fireMax, paint); //disparador maximo
          paint.setColor(Color.BLACK);
          mCanvas.drawLine(xDrawing, yDrawing + centerValue, xDrawing + 30, yDrawing + centerValue, paint); //centro
          paint.setColor(Color.GRAY);
          mCanvas.drawLine(xDrawing, yDrawing + fireMin, xDrawing + 30, yDrawing + fireMin, paint); //disparador minimo
          paint.setColor(Color.GREEN);
          mCanvas.drawLine(xDrawing, yDrawing + minValue, xDrawing + 30, yDrawing + minValue, paint); //minimo

		return mCanvas;
    }
   
}

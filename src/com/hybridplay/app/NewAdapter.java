package com.hybridplay.app;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressWarnings("unchecked")
public class NewAdapter extends BaseExpandableListAdapter{

	 public ArrayList<String> groupItem, tempChild;
	 public ArrayList<String> GroupItemDescription;
	 public ArrayList<Object> Childtem = new ArrayList<Object>();
	 public LayoutInflater minflater;
	 public Activity activity;

	 public NewAdapter(ArrayList<String> grList, ArrayList<String> groupItemDescription, ArrayList<Object> childItem) {
	  groupItem = grList;
	  this.GroupItemDescription = groupItemDescription;
	  this.Childtem = childItem;
	 }

	 public void setInflater(LayoutInflater mInflater, Activity act) {
	  this.minflater = mInflater;
	  activity = act;
	 }

	 @Override
	 public Object getChild(int groupPosition, int childPosition) {
	  return null;
	 }

	 @Override
	 public long getChildId(int groupPosition, int childPosition) {
	  return 0;
	 }

	 @Override
	 public View getChildView(final int groupPosition, final int childPosition,
			 boolean isLastChild, View convertView, ViewGroup parent) {
		 
		  tempChild = (ArrayList<String>) Childtem.get(groupPosition);
		  //TextView text = null;
		  if (convertView == null) {
		   convertView = minflater.inflate(R.layout.childrow, null);
		  }
	  
		  TextView text = (TextView) convertView.findViewById(R.id.childtext);
		  text.setTextColor(Color.BLACK);
		 // text.setText(tempChild.get(childPosition));
		  
	  
		  ImageView iconPark = (ImageView)convertView.findViewById(R.id.iconpark);
		  ImageView iconMove = (ImageView)convertView.findViewById(R.id.iconmove);
		  
		  if(tempChild.get(childPosition).equals("Balancin")){
			  iconPark.setImageResource(R.drawable.balancin);
	    	  iconMove.setImageResource(R.drawable.movflor);
	    	  text.setText("UP DOWN LEFT RIGHT");
		  }else if(tempChild.get(childPosition).equals("Caballito")){
			  iconPark.setImageResource(R.drawable.caballito);
	    	  iconMove.setImageResource(R.drawable.movflor);
	    	  text.setText("UP DOWN LEFT RIGHT");
		  }else if(tempChild.get(childPosition).equals("Columpio")){
			  iconPark.setImageResource(R.drawable.columpio);
	    	  iconMove.setImageResource(R.drawable.movcolumpio);
	    	  text.setText("Inclination");
		  }else if(tempChild.get(childPosition).equals("Rueda")){
			  iconPark.setImageResource(R.drawable.rueda);
	    	  iconMove.setImageResource(R.drawable.movrueda);
	    	  text.setText("Rotation");
		  }else if(tempChild.get(childPosition).equals("SubeBaja")){
			  iconPark.setImageResource(R.drawable.subebaja1);
	    	  iconMove.setImageResource(R.drawable.movsubebaja);
	    	  text.setText("UP DOWN");
		  }else if(tempChild.get(childPosition).equals("Tobogan")){
			  iconPark.setImageResource(R.drawable.tobogan);
	    	  iconMove.setImageResource(R.drawable.movtobogan);
	    	  text.setText("Jump");
		  }else if(tempChild.get(childPosition).equals("SubeBaja")){ //sube baja
			  iconPark.setImageResource(R.drawable.subebaja1);
	    	  iconMove.setImageResource(R.drawable.movsubebaja);
	    	  text.setText("UP DOWN");
		  }else if(tempChild.get(childPosition).equals("SubeBaja")){
			  iconPark.setImageResource(R.drawable.subebaja1);
	    	  iconMove.setImageResource(R.drawable.movsubebaja);
	    	  text.setText("UP DOWN");
		  }else if(tempChild.get(childPosition).equals("movsubebaja")){
			  iconPark.setImageResource(R.drawable.subebaja1);
	    	  iconMove.setImageResource(R.drawable.movtobogan);
	    	  text.setText("UP DOWN");
		  }

	  return convertView;
	 }


	 @Override
	 public int getChildrenCount(int groupPosition) {
	  return ((ArrayList<String>) Childtem.get(groupPosition)).size();
	 }

	 @Override
	 public Object getGroup(int groupPosition) {
	  return null;
	 }

	 @Override
	 public int getGroupCount() {
	  return groupItem.size();
	 }

	 @Override
	 public void onGroupCollapsed(int groupPosition) {
	  super.onGroupCollapsed(groupPosition);
	 }

	 @Override
	 public void onGroupExpanded(int groupPosition) {
	  super.onGroupExpanded(groupPosition);
	 }

	 @Override
	 public long getGroupId(int groupPosition) {
	  return 0;
	 }

	 @Override
	 public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		  if (convertView == null) {
		   convertView = minflater.inflate(R.layout.game_list_element, null);
		  }

		  //titulo del juego
		  TextView textView =(TextView)convertView.findViewById(R.id.titulo);
	      textView.setText(groupItem.get(groupPosition));
	      textView.setTextColor(Color.BLACK);
	     
	      //descripcion
		  TextView textView1 =(TextView)convertView.findViewById(R.id.subtitulo);
	      textView1.setText(GroupItemDescription.get(groupPosition));
	      textView1.setTextColor(Color.BLACK);
	      
	      ImageView imageIcono =(ImageView)convertView.findViewById(R.id.icono);
	      ImageView imgGame = (ImageView)convertView.findViewById(R.id.imgGame);

	      switch (groupPosition){
	      
	      case 0:
	    	  	 imageIcono.setImageResource(R.drawable.iconospace);
	    	  	 imgGame.setImageResource(R.drawable.special);
	             //textView1.setText("Collect space trash to clean the galaxy");
	             break;
	      case 1:
	    	  	 imageIcono.setImageResource(R.drawable.iconopacman);
	    	  	 imgGame.setImageResource(R.drawable.clasic);
	    	  	 //textView1.setText("Classic PacMan adapted to Hybrid Play");
	          	 break;
	      case 2:
	    	  	  imageIcono.setImageResource(R.drawable.iconopong);
	    	  	imgGame.setImageResource(R.drawable.clasic);
	              //textView1.setText("Classic Pong adapted to Hybrid Play");
	              break;
	      case 3:
	    	  	  imageIcono.setImageResource(R.drawable.iconopuzzle);
	    	  	  imgGame.setImageResource(R.drawable.special);
	              //textView1.setText("Find puzzle pieces and change the city");
	              break;
	      case 4:
	    	  	  imageIcono.setImageResource(R.drawable.iconoarcanoid);
	    	  	  imgGame.setImageResource(R.drawable.clasic);
	              //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	              break;
	      case 5:
			  	  imageIcono.setImageResource(R.drawable.iconorobot);
			  	  imgGame.setImageResource(R.drawable.madekids);
	          //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	          break;
	      case 6:
			  	  imageIcono.setImageResource(R.drawable.iconosnorkel);
			  	  imgGame.setImageResource(R.drawable.madekids);
	          //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	          break;
	      case 7: // SpaceInvaders
		  	  imageIcono.setImageResource(R.drawable.iconoconfig);
		  	  imgGame.setImageResource(R.drawable.clasic);
	          //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	          break;
	      case 8: // Tron
		  	  imageIcono.setImageResource(R.drawable.iconoconfig);
		  	  imgGame.setImageResource(R.drawable.clasic);
	          //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	          break;
	      case 9: // Config
		  	  imageIcono.setImageResource(R.drawable.iconoconfig);
		  	  imgGame.setImageResource(R.drawable.configbig);
	          //textView1.setText("Classic Arcanoid adapted to Hybrid Play");
	          break;          
	      default:
	    	  	 imageIcono.setImageResource(R.drawable.ic_launcher);
	             break;
	      }
	      return convertView;
	 }

	 @Override
	 public boolean hasStableIds() {
	  return false;
	 }

	 @Override
	 public boolean isChildSelectable(int groupPosition, int childPosition) {
	  return true;
	 }

	 
}

package com.hybridplay.app;


import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class GamesExpandable extends ExpandableListActivity {
	
	 ArrayList<String> groupItem = new ArrayList<String>();
	 ArrayList<Object> childItem = new ArrayList<Object>();
	 ArrayList<String> groupItemDescription = new ArrayList<String>();
	 
	 
	 
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		  setContentView(R.layout.gameslist);
		
		  ExpandableListView expandbleLis = getExpandableListView();
		  //expandbleLis.setDividerHeight(2);
		  expandbleLis.setGroupIndicator(null);
		  expandbleLis.setClickable(true);
		
		  setGroupData();
		  setGroupDescription();
		  setChildGroupData();
		  
		  NewAdapter mNewAdapter = new NewAdapter(groupItem, groupItemDescription, childItem);
		  mNewAdapter
		    .setInflater(
		      (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
		      this);
		  //getExpandableListView().setAdapter(mNewAdapter);
		  expandbleLis.setAdapter(mNewAdapter);
		 // expandbleLis.setOnChildClickListener(this);
		  registerForContextMenu(expandbleLis);
		  
		  expandbleLis.setOnGroupClickListener(new OnGroupClickListener(){

			@Override
			public boolean onGroupClick(ExpandableListView expListView, View arg1,
					int groupPos, long arg3) {
				// TODO Auto-generated method stub

				//Log.i("log groupPos", Integer.toString(groupPos));

				//expListView.getCount();
//				if(expListView.isGroupExpanded(groupPos-1)){
//					expListView.collapseGroup(groupPos-1);
//				}
				
				return false;
				}
		  	});
		  

		  expandbleLis.setOnChildClickListener(new OnChildClickListener(){
			  
				 @Override
				 public boolean onChildClick(ExpandableListView parent, View v,
				   int groupPosition, int childPosition, long id) {
				
					 //l.makeText(GamesExpandable.this, "Clicked On Child",
					 //Toast.LENGTH_SHORT).show();
					 
					 if (groupPosition == 0){
						   startSpaceKid(childPosition);
					   }else if (groupPosition == 1){
						   startPacMan(childPosition);
					   }else if (groupPosition == 2){
						   startPong(childPosition);
					   }else if (groupPosition == 3){
						   startPuzzleCity(childPosition);
					   }else if (groupPosition == 4){
						   startArcaDroid(childPosition);
					   }else if (groupPosition == 5){
						   startBuild(childPosition);
					   }else if (groupPosition == 6){
						   startFising(childPosition);
					   }else if (groupPosition == 7){
						   startConfig(childPosition);
					   }
				  return true;
				 }
		  });
		    

	}
	
	public void setGroupData() {	  
		  groupItem.add("SpaceKid");
		  groupItem.add("PackMan");
		  groupItem.add("Pong");
		  groupItem.add("PuzzleCity");
		  groupItem.add("Arkanoid");
		  groupItem.add("Building Something");
		  groupItem.add("Fishing");
		  groupItem.add("Config");
		 }
	
	public void setGroupDescription() {
		groupItemDescription.add("Collect space trash to clean the galaxy");
		groupItemDescription.add("Classic PacMan adapted to Hybrid Play");
		groupItemDescription.add("Classic Pong adapted to Hybrid Play");
		groupItemDescription.add("Find puzzle pieces and change the city");
		groupItemDescription.add("Classic Arkanoid adapted to Hybrid Play");
		groupItemDescription.add("Collect the pieces to build objects");
		groupItemDescription.add("Collect pearls");
		groupItemDescription.add("Sensor data visualization");
		}
	

	 public void setChildGroupData() {
		  /**
		   * Add Data For SpaceKid
		   */
		  ArrayList<String> child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("SubeBaja");
		  
		  childItem.add(child);

		  /**
		   * Add Data For PackMan
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  
		  childItem.add(child);
		  /**
		   * Add Data For Pong
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("SubeBaja");
		  
		  childItem.add(child);
		  /**
		   * Add Data For PuzzleCity
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("Columpio");
		  child.add("Tobogan");
		  child.add("SubeBaja");
		  
		  childItem.add(child);
		  
		  /**
		   * Add Data For Arcanoid
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("SubeBaja");
		  
		  childItem.add(child);
		  
		  /**
		   * Add Data For Building
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("SubeBaja");
		  
		  childItem.add(child);
		  
		  /**
		   * Add Data For Fishing
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
		  child.add("Caballito");
		  child.add("SubeBaja");
		  
		  childItem.add(child);
		  
		  /**
		   * Add Data For Config
		   */
		  child = new ArrayList<String>();
		  child.add("Balancin");
//		  child.add("Caballito");
//		  child.add("SubeBaja");
		  
		  childItem.add(child);
		 }
	 

	public void startSpaceKid(int position) {
		Intent sGame = new Intent(this, com.hybridplay.spacekids.propeller.GameActivitySpaceKidsPropeller.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(0);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
		
	public void startPacMan(int position){
		Intent sGame = new Intent(this, com.hybridplay.packman.GameActivityPackMan.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(1);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	public void startPong(int position) {
		Intent sGame = new Intent(this, com.hybridplay.pong.Pong.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(2);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
		
	public void startPuzzleCity(int position){
		Intent sGame = new Intent(this, com.hybridplay.puzzlecity.GameActivityPuzzleCity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(3);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
		
	public void startArcaDroid(int position){
		Intent sGame = new Intent(this, com.hybridplay.arkanoid.ArkaNoid.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(4);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	public void startBuild(int position){
		Intent sGame = new Intent(this, com.hybridplay.buildsomething.BuildSomethingActivity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(5);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	public void startFising(int position){
		Intent sGame = new Intent(this, com.hybridplay.fishing.GameActivityFishing.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(6);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	public void startConfig(int position){
		Intent sGame = new Intent(this, com.hybridplay.config.ConnectActivity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(7);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

}
	
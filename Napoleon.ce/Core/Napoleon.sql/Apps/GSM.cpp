/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Модуль GSM координат
 * 
 *  ert   30/04/2011   creating
 */ 

#include "stdafx.h"
#include <vector>

#define DEFINE_EXPORT
#include "AppsModule.h"

GSMModule::GSMModule() : hLib(0)
{
}

bool GSMModule::Init()
{
   if( state == stInit )
      return false;

   state = stInit;
   if( RILInit() == false )
   {
      state = stFail;
      return false;
   }

   state = stWork;
   return true;
}

void GSMModule::Stop()
{
   StopLocation();
   if( hLib != NULL )
      RILClose();

   state = stOff;
}

bool GSMModule::Do(Location *location)
{
   if( state != stWork )
   {
      if( !Init() )
         return false;
   }

   DWORD mcc, mnc, lac, cell;
   if( !GetMCC_MNC(&mcc, &mnc) )
   {
      Log("Error get MCC & MNC");
      state = stFail;
      return false;
   }

   if( !GetLAC_CellID(&lac, &cell) )
   {
      Log("Error get LAC & CellID");
      state = stFail;
      return false;
   }

   if( !GetLocation(&location->longitude, &location->latitude, mcc, mnc, lac, cell) )
      return false;

   location->speed = 0;
   location->isGPS = false;

   Log("GSM LAC=%d, CELL=%d (%d.%05d %d.%05d)", lac, cell, 
      location->longitude / GPS_SCALE, location->longitude % GPS_SCALE,
      location->latitude / GPS_SCALE , location->latitude % GPS_SCALE);

   return true;
}

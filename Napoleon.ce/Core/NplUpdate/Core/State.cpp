/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* State
*
*  ert   09/11/2009   creating
*/
#include "stdafx.h"
#include "NplUpdate.h"
#include <vector>
#include <algorithm>

using namespace std;

typedef vector<State*> States;

#pragma warning(disable : 4073)
#pragma init_seg(lib)
States states;

State* State::Find(const wchar_t* state)
{
   States::iterator i = states.begin();
   for( ; i != states.end(); i++ )
   {
      State* ps = (*i);
      if( wcscmp(ps->Name(), state) == 0 )
         return ps;
   }

   return NULL;
}

void State::WriteState(FILE* wr) const
{
   std::wstring stateName(name);
   WriteString(wr, stateName);

   Write(wr);
}

bool State::Load(ProgConfig* config, FILE *rd)
{
   bool retVal = false;
   std::wstring stateName;

   if( ReadString(rd, &stateName) )
   {
      config->curState = Find(stateName.c_str());
      if( config->curState != NULL )
         retVal = config->curState->Load(rd);
      else
         Log("Find state error");
   } else
   {
      Log("Load state error");
   }

   return retVal;
}

void State::AddState(State *state)
{
   states.push_back(state);
}

void State::RemoveState(State *state)
{
   States::iterator i = find(states.begin(), states.end(), state);
   if( i != states.end() )
      states.erase(i);
}

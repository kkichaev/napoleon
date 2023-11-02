/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 * 
 * Интерфесы
 * 
 * ert   02/09/2010   creating
 */
namespace GRSoft.Network
{
   public interface IProgress
   {
      void SetText(string text);

      void SetMax(int max);

      void AdvancePos(int pos);
   }
}
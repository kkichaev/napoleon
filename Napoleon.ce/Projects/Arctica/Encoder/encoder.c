#include <windows.h>
#include <stdio.h>

int main(int argc, char* argv[])
{
   int len;
   char buf[100];
   FILE *in, *out;
   if( argc != 3 )
      return 1;

   in = fopen(argv[1], "rt");
   out = fopen(argv[2], "wt");

   if( !in || !out )
      return 1;

   while(1)
   {
      len = fread(buf, sizeof(char), sizeof(buf) / sizeof(char), in);
      if( len <= 0 )
         break;

      OemToCharBuff(buf, buf, len);
      fwrite(buf, sizeof(char), len, out);
   }

   fclose(in);
   fclose(out);
   return 0;
}
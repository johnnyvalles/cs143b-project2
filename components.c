#include <stdio.h>
#include <inttypes.h>
#include <ctype.h>
#include <stdlib.h>

unsigned int convert(char *str);

int main(int argc, char** argv) {

    for (int i = 1; i < argc; ++i) {
        uint32_t va = convert(argv[i]);
        uint32_t pa = 0;
        
        uint32_t s = va >> 18;
        uint32_t p = ((va >> 9) & 0x1FF);
        uint32_t w = va & 0x1FF;
        uint32_t pw = va & 0x3FFFF;
        
        printf("s:   %" PRIu32 "\n", s);
        printf("p:   %" PRIu32 "\n", p);
        printf("w:   %" PRIu32 "\n", w);
        printf("pw:  %" PRIu32 "\n", pw);

        printf("VA:  %" PRIu32 "\n", va);
        printf("PA:  %" PRIu32 "\n", pa);

        getc(stdin);
    }

    return 0;
}

unsigned int convert(char *str) {
  char *x;
  for (x = str; *x; ++x) {
    if (!isdigit(*x))
      return 0L;
  }
  return (strtoul(str, 0L, 10));
}

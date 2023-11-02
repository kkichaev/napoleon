<template>
  <div app class="text-center">
    <v-menu offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          class="primary"
          text
          v-bind="attrs"
          v-on="on">
          {{$i18n.locale}}
        </v-btn>
      </template>
      <v-list>
        <v-list-item
          v-for="lang in $i18n.availableLocales"
          :key="lang">

          <v-list-item-title @click="$i18n.locale = lang">{{ lang }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script>
 
 export default{
  mounted(){
    
  }
 }
</script>
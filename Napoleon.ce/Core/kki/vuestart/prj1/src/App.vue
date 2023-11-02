<template>
  <div>
    <DlgAlret :md-active.sync="first"/>

    <md-dialog-alert
      :md-active.sync="second"
      md-title="Post created!"
      md-content="Your post <strong>Material Design is awesome</strong> has been created." />

    <md-button class="md-accent md-raised" @click="first = true">Alert</md-button>
    <md-button class="md-primary md-raised" @click="second = true">Alert</md-button>
  </div>
</template>

<script>
  import DlgAlret from './Dlg.vue'

  export default {
    name: 'DialogAlert',
    components:{
      DlgAlret
    },
    data: () => ({
      first: false,
      second: false
    })
  }
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

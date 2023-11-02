<template>
    <div>
        <v-btn :outlined="!agentMode" color="secondary" class="ma-3" @click="agentMode = true">Агенты</v-btn>
        <v-btn :outlined="agentMode" color="secondary" class="ma-3" @click="agentMode = false">Менеджеры</v-btn>
    </div>
</template>
<script>
export default{
    data(){
        return{
            agentMode: true
        }
    },
    watch:{
        agentMode(){
            console.log('agentMode changed: ', this.agentMode)
            this.$emit("onChanged", this.agentMode)
        }
    }
}
</script>
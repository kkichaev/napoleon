import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)


export default function createStore(){
    var res = new Vuex.Store({
        state:{
            agentResource: null,
            loginResource: null,
            agents: [],
            error: "some error!"
        },
        mutations:{
            setAgent(state, payload){
                state.agents = payload
            },
            setToken(state, token){
                localStorage.token = token
            },
            clearError(state){
                state.error = ""
            }
        },
        actions:{
            loadAgent(context){
                this.agentResource.get().then(r=>r.json()).then(a=>context.commit('setAgent', a))
            },
            async loginUser({commit}){
                try{
                    const res = await this.loginResource.save('/login', {email:"test@test.com", password:"test"})
                    commit('setToken', res.body.accessToken)
                    return res
                }catch(error){
                    console.log('trow error: ', error)
                    throw error
                }
            }
        }

    })

    res.agentResource = Vue.resource('agents')
    res.loginResource = Vue.resource('login')

    return res;
}
package com.example.listatareas

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listatareas.db.AppDatabase
import com.example.listatareas.db.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // operacion bases de datos dentro de una corrutina
        lifecycleScope.launch(Dispatchers.IO) {
            val tareaDao =AppDatabase.getInstance(this@MainActivity).tareaDao()
            val cantRegistros = tareaDao.contar()
            if (cantRegistros > 1) {
                tareaDao.insertTarea(Tarea(0, "Lavar Ropa", false))
                tareaDao.insertTarea(Tarea(1, "Planchar", false))
                tareaDao.insertTarea(Tarea(2, "Compra azucar", false))
            }
        }

        setContent {

            ListaTareaUI()

        }
    }
}

@Composable
fun ListaTareaUI(){
        val context = LocalContext.current
        // variable de estado mantiene el estado de los valores
        val (tareas, setTareas)= remember {
            mutableStateOf(emptyList<Tarea>())
        }
        LaunchedEffect(tareas){
            withContext(Dispatchers.IO){
                val dao = AppDatabase.getInstance(context).tareaDao()
                setTareas(dao.getAll())
            }

        }

    LazyColumn( modifier = Modifier.fillMaxSize())
    {
        items(tareas){tarea ->
            TareaItemUI(tarea = tarea){
                    setTareas(emptyList<Tarea>())
            }

        }

    }


}

@Composable
fun TareaItemUI(tarea:Tarea, onSave:()-> Unit = {}) {
    val alcanceCorrutina = rememberCoroutineScope()
    val contexto = LocalContext.current

    Row (
        modifier= Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        if(tarea.realizada){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Tarea Realizada",
                modifier =Modifier.clickable {
                    alcanceCorrutina.launch( Dispatchers.IO){
                        val dao= AppDatabase.getInstance(contexto).tareaDao()
                        tarea.realizada = false
                        dao.updateTarea(tarea)
                        onSave()
                    }

                }
            )
        }else{
            Icon(
                Icons.Filled.Face,
                contentDescription = "tarea Por hacer",
                modifier =Modifier.clickable {
                    alcanceCorrutina.launch( Dispatchers.IO){
                        val dao= AppDatabase.getInstance(contexto).tareaDao()
                        tarea.realizada = true
                        dao.updateTarea(tarea)
                        onSave()
                    }

                }
            )

        }
        Spacer(modifier= Modifier.width(20.dp))
            Text(
                text=tarea.tarea,
                modifier= Modifier.weight(2f))
        Icon(
            Icons.Filled.Delete,
            contentDescription = "ELIMINAR TAREAS",
            modifier =Modifier.clickable {
                alcanceCorrutina.launch( Dispatchers.IO){
                    val dao= AppDatabase.getInstance(contexto).tareaDao()
                    dao.deleteTarea(tarea)
                    onSave()
                }

            }
        )
        Icon(
            Icons.Filled.Edit,
            contentDescription = "mODIFICAR TAREA"
        )
    }
}




@Preview(showBackground = true)
@Composable
fun TareaItemUIPrevie(){
    val tarea= Tarea(1 ,"Limpiar el patio", true)
    TareaItemUI(tarea = tarea)

}
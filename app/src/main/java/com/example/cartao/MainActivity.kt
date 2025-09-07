package com.example.cartao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartao.ui.theme.CartaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CartaoTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CartaoDeVisitas()
                }
            }
        }
    }
}

@Composable
fun CartaoDeVisitas() {
    Card(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Parte Superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(red = 12, green = 49, blue = 82, alpha = 255))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Marcos Paulo Pires Silva",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Tecnólogo em Sistemas para Internet",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            // Parte Inferior (Branca)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(red = 238, green = 238, blue = 238, alpha = 65))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                ContatoItem(R.drawable.baseline_phone_24, "(84) 996278664")
                ContatoItem(R.drawable.outline_alternate_email_24, "marcos.paulo.spires@mail.com")

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Seção de Redes Sociais
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ContatoItemHorizontal(R.drawable.ic_github, "GitHub/mrc-p")
                }
            }
        }
    }
}

@Composable
fun ContatoItem(iconId: Int, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = texto, fontSize = 16.sp)
    }
}

@Composable
fun ContatoItemHorizontal(iconId: Int, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CartaoPreview() {
    CartaoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CartaoDeVisitas()
        }
    }
}
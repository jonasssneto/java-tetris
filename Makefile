.PHONY: dev build clean test run help

# Comando principal para desenvolvimento
dev: build run

# Compila o projeto
build:
	@echo "Compilando o projeto"
	@mvn clean package -DskipTests

# Executa o jogo
run:
	@echo "Iniciando"
	@java -jar target/tetris.jar

# Compila e roda os testes
test:
	@echo "Executando testes"
	@mvn test

# Limpa os arquivos compilados
clean:
	@echo "Limpando arquivos compilados"
	@mvn clean
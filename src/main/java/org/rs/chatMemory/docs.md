# What is ChatMemory?
ChatMemory represents conversational state in LangChain4j. It stores past user and AI messages and ensures that each new model invocation receives the relevant conversation context.

# Why ChatMemory is needed
Without memory, each interaction is stateless. ChatMemory allows the model to recall facts introduced earlier, maintain continuity, and produce coherent multi-turn conversations.
Check out basic LLM memory tutorial here.

**Common use cases**
* Multi-step user conversations
* Chatbots requiring context awareness
* Interactive assistants with session-level state

**We can use a simple List<ChatMessage> to create a multi-turn conversation (check out this example),
but that is not same as using ChatMemory.
In this tutorial we are going to see what the difference is.**

# Core Difference
List<ChatMessage> is just a data container, while ChatMemory is a managed conversation state system.

# List<ChatMessage> (Manual Control)
A simple collection you manage completely yourself.

List<ChatMessage> messages = new ArrayList<>();

**Your Responsibilities:**
* Adding messages in correct order
* Removing old messages manually
* Preventing unlimited growth
* Managing session separation

**Limitations:**
* No automatic message trimming
* Easy to exceed token limits
* No built-in strategies
* No persistence support

# ChatMemory (Managed System)
A contract defining how conversation history is managed.

ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

# Advantages:
* Automatic message windowing
* Pluggable strategies (window, token-based, persistent)
* Built-in safety controls
* Session-aware design

# When to Use Each
**Use List<ChatMessage> for:**
* Single-turn or very short conversations
* Demos and examples
* When you need absolute manual control

**Use ChatMemory for:**
* Real applications
* Multi-turn conversations
* Multiple users/sessions
* When you need persistence or scaling

# Bottom Line
List<ChatMessage> is simple storage. ChatMemory is storage plus policy, lifecycle, and safety management.


# What is MessageWindowChatMemory?
There are currently two ChatMemory implementations in LangChain4j:

* MessageWindowChatMemory
* TokenWindowChatMemory

We have already seen an example of MessageWindowChatMemory. In this tutorial we are going to understand the purpose of MessageWindowChatMemory.

MessageWindowChatMemory is a ChatMemory implementation that keeps only a fixed number of the most recent messages. Older messages are automatically discarded.

# Why message windowing matters
Large conversations increase token usage and cost. Message windowing provides predictable memory growth and protects applications from exceeding model limits.

**Typical use cases**
* Short-lived conversational flows
* Chatbots with limited context needs
* Cost-sensitive applications


# What is TokenWindowChatMemory?
TokenWindowChatMemory is another implementation of ChatMemory This implementation limits conversation history based on the total number of tokens rather than message count.

# Why token-based memory?
Messages vary in length. Token-based memory ensures that the prompt always fits within model constraints regardless of message size.

# What is a token?
A token is the basic unit of text that a language model processes—typically a word, part of a word, or a single character—and is used to measure and limit conversation history to ensure the total input stays within the model’s length constraints.

**Typical use cases**
* Conversations with variable-length messages
* Strict token budget enforcement
* Production systems with predictable costs


# What is persistent ChatMemory?
Persistent ChatMemory stores conversation history outside application memory, allowing conversations to survive restarts.

# Why persistence is important
Stateless deployments and distributed systems require memory to be externalized. Persistent memory enables long-lived user sessions.

**Typical use cases**
* User chat history retention
* Multi-instance deployments
* Session recovery
* Understanding ChatMemoryStore

In LangChain4j, the ChatMemoryStore interface represents the persistence layer for your conversation history. While ChatMemory acts as the manager that decides which messages to keep (based on logic like a sliding window), the Store is the actual physical or virtual location where those messages are saved and retrieved. This separation of concerns allows you to swap out how data is stored without changing your application's logic.

# InMemoryChatMemoryStore
LangChain4j provides one public implementation of ChatMemoryStore, which is InMemoryChatMemoryStore. This storage mechanism is transient and does not persist data across application restarts.